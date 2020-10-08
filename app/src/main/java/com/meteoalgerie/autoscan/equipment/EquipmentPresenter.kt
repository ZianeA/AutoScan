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
import de.timroes.axmlrpc.XMLRPCException
import hu.akarnokd.rxjava2.operators.ObservableTransformers
import hu.akarnokd.rxjava2.subjects.UnicastWorkSubject
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
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

    val message: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
    val clearBarcodeBox: UnicastWorkSubject<Unit> = UnicastWorkSubject.create()
    val displayEquipmentMoved: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
    val animateEquipment: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
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
        barcode.toIntOrNull()
            ?: throw IllegalArgumentException("Equipment barcode must contain only numeric values")
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
                            if (result.equipment.deskId != result.equipment.previousDeskId) {
                                displayEquipmentMoved.onNext(id)
                            }
                            // Animate equipment
                            animateEquipment.onNext(id)
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
        when {
            barcode.length < 5 -> return
            barcode.length == EQUIPMENT_BARCODE_LENGTH -> scanBarcode(barcode, deskId)
            else -> throw IllegalArgumentException("Equipment barcode must not be more than 5 digits long")
        }
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
                    .onErrorReturn { ScanResult.Error(scanResult.equipment, it) }
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

    companion object {
        private const val EQUIPMENT_BARCODE_LENGTH = 5
    }
}