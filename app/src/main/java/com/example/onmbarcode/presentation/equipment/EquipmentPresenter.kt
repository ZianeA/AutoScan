package com.example.onmbarcode.presentation.equipment

import androidx.preference.PreferenceManager
import com.example.onmbarcode.data.KeyValueStore
import com.example.onmbarcode.data.PreferencesStringSetStore
import com.example.onmbarcode.data.desk.DeskRepository
import com.example.onmbarcode.data.equipment.EquipmentRepository
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.Clock
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import com.example.onmbarcode.service.SyncBackgroundService
import de.timroes.axmlrpc.XMLRPCException
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.lang.IllegalArgumentException
import javax.inject.Inject

@FragmentScope
class EquipmentPresenter @Inject constructor(
    private val view: EquipmentView,
    private val equipmentRepository: EquipmentRepository,
    private val deskRepository: DeskRepository,
    @JvmSuppressWildcards private val store: KeyValueStore<Set<String>>,
    private val syncService: SyncBackgroundService,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()

    fun start(desk: Desk) {
        val disposable =
            store.observe(PreferencesStringSetStore.EQUIPMENT_FILTER_KEY, defaultSelectedTags)
                .switchMap { tags ->
                    equipmentRepository.getEquipmentForDeskWithScanState(
                        desk.id,
                        *tags.map { ScanState.valueOf(it) }.toTypedArray()
                    )
                }
                .flatMapSingle { e ->
                    deskRepository.getDeskById(desk.id)
                        .map {
                            object {
                                val desk: Desk = it
                                val equipment: List<Equipment> = e
                            }
                        }
                }
                .applySchedulers(schedulerProvider)
                .subscribe({
                    if (view.isScrolling.not()) {
                        view.displayEquipments(it.desk, it.equipment, selectedTags)
                    }
                }, { /*onError*/ })

        disposables.add(disposable)
    }

    fun onScrollEnded(deskId: Int) {
        val disposable = equipmentRepository.getEquipmentForDeskWithScanState(
            deskId,
            *selectedTags.map { ScanState.valueOf(it) }.toTypedArray()
        )
            .first(emptyList()) //Unsubscribe after the first emitted item
            .flatMap { e ->
                deskRepository.getDeskById(deskId)
                    .map {
                        object {
                            val desk: Desk = it;
                            val equipment: List<Equipment> = e
                        }
                    }
            }
            .applySchedulers(schedulerProvider)
            .subscribe({
                if (view.isScrolling.not()) view.displayEquipments(
                    it.desk,
                    it.equipment,
                    selectedTags
                )
            }, { /*onError*/ })

        disposables.add(disposable)
    }

    // TODO add more unit tests, notably for error messages
    private fun scanBarcode(barcode: String, deskId: Int) {
        barcode.toIntOrNull()
            ?: throw IllegalArgumentException(
                "Malformed equipment barcode. Equipment barcode must contain only numeric values"
            )
        view.clearBarcodeInputArea()

        val disposable = equipmentRepository.findEquipment(barcode)
            .observeOn(schedulerProvider.main)
            .doOnEvent { e, t -> if (e == null && t == null) view.showUnknownBarcodeMessage() }
            .flatMap {
                if (it.scanState != ScanState.NotScanned) {
                    view.showEquipmentAlreadyScannedMessage()
                    Maybe.empty()
                } else {
                    view.displayProgressBarForEquipment(it.id)
                    if (view.isScrolling.not()) {
                        view.scrollToTop()
                    }
                    Maybe.just(it)
                }
            }
            .observeOn(schedulerProvider.worker)
            .flatMap { scannedEquipment ->
                val updatedEquipment =
                    scannedEquipment.copy(scanDate = clock.currentTimeSeconds, deskId = deskId)
                equipmentRepository.updateEquipment(updatedEquipment)
                    .andThen(Maybe.just(updatedEquipment))
                    .onErrorResumeNext { it: Throwable ->
                        when (it) {
                            is XMLRPCException -> {
                                //TODO should probably do this only if it's a no internet connexion exception
                                syncService.syncEquipments()
                                Maybe.just(updatedEquipment)
                            }
                            else -> {
                                // This is probably a serious error
                                Maybe.error(it)
                            }
                        }
                    }
            }
            .doOnDispose { syncService.syncEquipments() } // Sync in the background if the scanning was interrupted
            .applySchedulers(schedulerProvider)
            .subscribe(
                {
                    view.hideProgressBarForEquipment(it.id)
                    view.animateEquipment(it.id)
                    if (it.deskId != it.previousDeskId) view.showEquipmentMovedMessage()
                },
                {
                    view.showErrorMessage()
                },
                { /*onComplete*/ }
            )

        disposables.add(disposable)
    }

    fun onBarcodeChange(barcode: String, deskId: Int) {
        when {
            barcode.length < 5 -> return
            barcode.length == EQUIPMENT_BARCODE_LENGTH -> scanBarcode(barcode, deskId)
            else -> throw IllegalArgumentException("Equipment barcode must not be more than 5 digits long")
        }
    }

    fun onEquipmentConditionPicked(conditionIndex: Int, equipment: Equipment) {
        view.displayProgressBarForEquipment(equipment.id)

        val disposable = equipmentRepository.updateEquipment(
            equipment.copy(
                condition = EquipmentCondition.getByValue(conditionIndex)
            )
        )
            .onErrorResumeNext {
                when (it) {
                    is XMLRPCException -> {
                        //TODO should probably do this only if it's a no internet connexion exception
                        syncService.syncEquipments()
                        Completable.complete()
                    }
                    else -> {
                        // This is probably a serious error
                        Completable.error(it)
                    }
                }
            }
            .doOnDispose { syncService.syncEquipments() } // Sync in the background if the scanning was interrupted
            .applySchedulers(schedulerProvider)
            .subscribe({
                view.hideProgressBarForEquipment(equipment.id)
                view.displayEquipmentConditionChangedMessage()
            },
                { view.showErrorMessage() })

        disposables.add(disposable)
    }

    fun onTagClicked(tag: ScanState) {
        val isTagSelected = selectedTags.find { it == tag.name } != null
        if (selectedTags.size == 1 && isTagSelected) return

        val disposable = Single.fromCallable {
            if (isTagSelected) {
                store.remove(
                    PreferencesStringSetStore.EQUIPMENT_FILTER_KEY,
                    setOf(tag.name),
                    defaultSelectedTags
                )
            } else {
                store.add(
                    PreferencesStringSetStore.EQUIPMENT_FILTER_KEY,
                    setOf(tag.name),
                    defaultSelectedTags
                )
            }
        }
            .applySchedulers(schedulerProvider)
            .subscribe({}, { /*onError*/ })

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }

    private val defaultSelectedTags = setOf(
        ScanState.ScannedAndSynced.name,
        ScanState.ScannedButNotSynced.name,
        ScanState.NotScanned.name
    )

    private val selectedTags: Set<String>
        get() {
            return store.get(PreferencesStringSetStore.EQUIPMENT_FILTER_KEY, defaultSelectedTags)
        }

    companion object {
        private const val EQUIPMENT_BARCODE_LENGTH = 5
    }
}