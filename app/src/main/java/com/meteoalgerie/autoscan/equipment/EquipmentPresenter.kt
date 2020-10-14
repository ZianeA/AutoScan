package com.meteoalgerie.autoscan.equipment

import androidx.room.EmptyResultSetException
import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.desk.DeskRepository
import com.meteoalgerie.autoscan.equipment.Equipment.*
import com.meteoalgerie.autoscan.desk.Desk
import com.meteoalgerie.autoscan.common.di.FragmentScope
import com.meteoalgerie.autoscan.common.util.Clock
import com.meteoalgerie.autoscan.common.scheduler.SchedulerProvider
import com.meteoalgerie.autoscan.common.util.Lce
import com.meteoalgerie.autoscan.common.util.toLce
import com.meteoalgerie.autoscan.equipment.service.SyncBackgroundService
import com.meteoalgerie.autoscan.settings.ScanMode
import de.timroes.axmlrpc.XMLRPCException
import hu.akarnokd.rxjava2.subjects.UnicastWorkSubject
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import kotlin.IllegalArgumentException

@FragmentScope
class EquipmentPresenter @Inject constructor(
    private val equipmentDesk: Desk,
    private val equipmentRepository: EquipmentRepository,
    private val deskRepository: DeskRepository,
    private val storage: PreferenceStorage,
    private val syncService: SyncBackgroundService,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()

    val equipment = BehaviorRelay.create<List<Equipment>>()
    val desk = BehaviorRelay.createDefault<Desk>(equipmentDesk)
    val selectedTags = BehaviorSubject.createDefault(storage.equipmentFilter)
    val isLoading = BehaviorRelay.create<Boolean>()
    val isRefreshing = BehaviorRelay.create<Boolean>()
    val scanningEquipment = BehaviorRelay.createDefault(emptyList<Int>())
    val isManualScan = BehaviorRelay.create<Boolean>()

    val message: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
    val clearBarcodeBox: UnicastWorkSubject<Unit> = UnicastWorkSubject.create()
    val displayEquipmentMoved: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
    val animateEquipment: UnicastWorkSubject<Pair<Int, AnimationType>> = UnicastWorkSubject.create()
    val scrollToTop: UnicastWorkSubject<Unit> = UnicastWorkSubject.create()

    fun start() {
        disposables += Observable.concat(
            equipmentRepository.refreshEquipmentForDesk(equipmentDesk.id).toLce<List<Equipment>>(),
            loadEquipment()
        )
            .subscribeOn(schedulerProvider.worker)
            .startWith(Lce.Loading())
            .subscribe { lce ->
                when (lce) {
                    is Lce.Loading -> isLoading.accept(true)
                    is Lce.Content -> {
                        isLoading.accept(false)
                        equipment.accept(lce.data)
                    }
                    is Lce.Error -> {
                        isLoading.accept(false)
                        if (lce.error is XMLRPCException) {
                            message.onNext(R.string.message_you_are_offline)
                        } else {
                            message.onNext(R.string.message_error_unknown)
                        }
                    }
                }
            }

        disposables += deskRepository.getDeskById(equipmentDesk.id)
            .subscribeOn(schedulerProvider.worker)
            .subscribe { desk.accept(it) }

        isManualScan.accept(storage.scanMode == ScanMode.MANUAL.name)
    }

    fun onRefresh(deskId: Int) {
        isRefreshing.accept(true)

        disposables += equipmentRepository.refreshEquipmentForDesk(deskId)
            .subscribeOn(schedulerProvider.worker)
            .subscribeBy(
                onComplete = { isRefreshing.accept(false) },
                onError = { error ->
                    isRefreshing.accept(false)

                    if (error is XMLRPCException) {
                        message.onNext(R.string.message_you_are_offline)
                    } else {
                        message.onNext(R.string.message_error_unknown)
                    }
                })
    }

    private fun scanBarcode(barcode: String, deskId: Int) {
        clearBarcodeBox.onNext(Unit)

        disposables += equipmentRepository.findEquipment(barcode)
            .map {
                if (it.scanState != ScanState.NotScanned) {
                    ScanResult.Error(it, IllegalArgumentException("Equipment already scanned"))
                } else {
                    ScanResult.Loading(it)
                }
            }
            .onErrorReturn { error -> ScanResult.Error(null, error) }
            .flatMapObservable { scanAndSynchronize(it, deskId) }
            .subscribeOn(schedulerProvider.worker)
            .subscribeBy(
                onNext = { result ->
                    when (result) {
                        is ScanResult.Loading -> {
                            // Add equipment to scanning list
                            scanningEquipment.accept(scanningEquipment.value!! + result.equipment.id)
                            scrollToTop.onNext(Unit)
                        }
                        is ScanResult.Success -> {
                            val id = result.equipment.id
                            // Remove equipment from scanning list
                            scanningEquipment.accept(scanningEquipment.value!! - id)

                            // Show equipment moved
                            if (result.equipment.deskId != result.equipment.previousDeskId) {
                                displayEquipmentMoved.onNext(id)
                            }
                            // Animate equipment
                            animateEquipment.onNext(id to AnimationType.SUCCESS)
                        }
                        is ScanResult.Error -> {
                            when (result.error) {
                                is EmptyResultSetException -> {
                                    message.onNext(R.string.message_error_unknown_barcode)
                                }
                                is IllegalArgumentException -> {
                                    message.onNext(R.string.message_error_equipment_already_scanned)
                                }
                                is XMLRPCException -> {
                                    syncService.syncEquipment()
                                    message.onNext(R.string.message_error_network)

                                    // Show equipment moved
                                    val equipment = result.equipment!!
                                    if (equipment.deskId != equipment.previousDeskId) {
                                        displayEquipmentMoved.onNext(equipment.id)
                                    }
                                    // Animate equipment
                                    animateEquipment.onNext(equipment.id to AnimationType.FAILURE)
                                }
                                else -> {
                                    message.onNext(R.string.message_error_unknown)
                                }
                            }

                            // Remove equipment from scanning list
                            result.equipment?.let {
                                scanningEquipment.accept(scanningEquipment.value!! - it.id)
                            }
                        }
                    }
                }
            )
    }

    sealed class ScanResult {
        data class Loading(val equipment: Equipment) : ScanResult()
        data class Error(val equipment: Equipment?, val error: Throwable) : ScanResult()
        data class Success(val equipment: Equipment) : ScanResult()
    }

    fun onBarcodeChange(barcode: String, deskId: Int) {
        if (storage.scanMode == ScanMode.MANUAL.name) return

        when {
            barcode.length < storage.barcodeLength -> return
            barcode.length == storage.barcodeLength -> scanBarcode(barcode, deskId)
            else -> clearBarcodeBox.onNext(Unit)
        }
    }

    fun onSubmitBarcode(barcode: String, deskId: Int) {
        if (storage.scanMode == ScanMode.AUTOMATIC.name) throw IllegalStateException("Cannot submit a barcode in auto-scan mode")

        scanBarcode(barcode, deskId)
    }

    fun onEquipmentConditionPicked(conditionIndex: Int, equipment: Equipment) {
        scanningEquipment.accept(scanningEquipment.value!! + equipment.id)

        disposables += equipmentRepository.updateEquipment(
            equipment.copy(condition = EquipmentCondition.getByValue(conditionIndex))
        )
            // Sync in the background if the scanning is interrupted
            .doOnDispose { syncService.syncEquipment() }
            .subscribeOn(schedulerProvider.worker)
            .subscribeBy(
                onComplete = {
                    scanningEquipment.accept(scanningEquipment.value!! - equipment.id)
                    message.onNext(R.string.message_equipment_condition_changed)
                },
                onError = {
                    scanningEquipment.accept(scanningEquipment.value!! - equipment.id)

                    if (it is XMLRPCException) {
                        syncService.syncEquipment()
                        message.onNext(R.string.message_error_network)
                    } else {
                        message.onNext(R.string.message_error_unknown)
                    }
                })
    }

    fun onTagClicked(tag: ScanState) {
        val isTagSelected = storage.equipmentFilter.any { it == tag.name }
        // At least one tag must be selected
        if (storage.equipmentFilter.size == 1 && isTagSelected) return

        if (isTagSelected) {
            storage.equipmentFilter -= tag.name
        } else {
            storage.equipmentFilter += tag.name
        }

        selectedTags.onNext(storage.equipmentFilter)
    }

    private fun loadEquipment() = selectedTags.switchMap { tags ->
        equipmentRepository.getEquipmentForDeskAndScanState(
            equipmentDesk.id,
            tags.map { ScanState.valueOf(it) })
    }
        .toLce()

    private fun scanAndSynchronize(scanResult: ScanResult, deskId: Int): Observable<ScanResult> {
        return when (scanResult) {
            is ScanResult.Error -> Observable.just(scanResult)
            is ScanResult.Loading -> {
                val updatedEquipment = scanResult.equipment.copy(
                    scanDate = clock.currentTimeSeconds,
                    deskId = deskId
                )
                equipmentRepository.updateEquipment(updatedEquipment)
                    .andThen(Observable.just(ScanResult.Success(updatedEquipment) as ScanResult))
                    .onErrorReturn { ScanResult.Error(updatedEquipment, it) }
                    // Start by emitting the loading state
                    .startWith(scanResult)
                    // Sync in the background if the scanning is interrupted
                    .doOnDispose { syncService.syncEquipment() }
            }
            else -> {
                throw IllegalStateException("ScanResult can't be a success at this stage")
            }
        }
    }

    fun onCleared() {
        disposables.clear()
    }

    enum class AnimationType { SUCCESS, FAILURE }
}