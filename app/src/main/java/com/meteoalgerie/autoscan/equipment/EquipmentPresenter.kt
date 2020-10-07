package com.meteoalgerie.autoscan.equipment

import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.desk.DeskRepository
import com.meteoalgerie.autoscan.equipment.Equipment.*
import com.meteoalgerie.autoscan.desk.Desk
import com.meteoalgerie.autoscan.common.di.FragmentScope
import com.meteoalgerie.autoscan.common.util.Clock
import com.meteoalgerie.autoscan.common.util.applySchedulers
import com.meteoalgerie.autoscan.common.scheduler.SchedulerProvider
import com.meteoalgerie.autoscan.equipment.service.SyncBackgroundService
import de.timroes.axmlrpc.XMLRPCException
import hu.akarnokd.rxjava2.operators.ObservableTransformers
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.BehaviorSubject
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class EquipmentPresenter @Inject constructor(
    private val view: EquipmentView,
    private val equipmentRepository: EquipmentRepository,
    private val deskRepository: DeskRepository,
    private val storage: PreferenceStorage,
    private val syncService: SyncBackgroundService,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()
    private val scrollingValve = PublishProcessor.create<Boolean>()
    private val filterSubject = BehaviorSubject.createDefault(storage.equipmentFilter)

    fun start(refresh: Boolean, desk: Desk) {
        if (refresh) view.showLoadingView()

        val disposable = Single.just(refresh)
            .flatMapCompletable {
                if (it) {
                    equipmentRepository.refreshEquipmentForDesk(desk.id)
                } else {
                    Completable.complete()
                }
            }
            .observeOn(schedulerProvider.main)
            // hide loading view regardless if completable completes normally or fails
            .doOnTerminate { view.hideLoadingView() }
            .onErrorResumeNext {
                if (it is XMLRPCException) {
                    view.showNetworkErrorMessage()
                    Completable.complete()
                } else {
                    Completable.error(it)
                }
            }
            .observeOn(schedulerProvider.worker)
            .andThen(filterSubject)
            .switchMap { tags ->
                equipmentRepository.getEquipmentForDeskAndScanState(
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
            .compose(ObservableTransformers.valve(scrollingValve.toObservable(), true))
            .applySchedulers(schedulerProvider)
            .subscribe(
                { view.displayEquipments(it.desk, it.equipment, storage.equipmentFilter) },
                { view.showErrorMessage() }
            )

        disposables.add(disposable)
    }

    fun onRefresh(deskId: Int) {
        val disposable = equipmentRepository.refreshEquipmentForDesk(deskId)
            .applySchedulers(schedulerProvider)
            .subscribe({ view.hideLoadingView() },
                {
                    view.hideLoadingView()

                    if (it is XMLRPCException) {
                        view.showNetworkErrorMessage()
                    } else {
                        view.showErrorMessage()
                    }
                })

        disposables.add(disposable)
    }

    fun onScrollEnded() {
        scrollingValve.onNext(true)
    }

    // TODO add more unit tests, notably for error messages
    private fun scanBarcode(barcode: String, deskId: Int) {
        barcode.toIntOrNull()
            ?: throw IllegalArgumentException(
                "Malformed equipment barcode. Equipment barcode must contain only numeric values"
            )
        view.clearBarcodeInputArea()
        var equipmentId: Int? = null

        val disposable = equipmentRepository.findEquipment(barcode)
            .observeOn(schedulerProvider.main)
            .doOnEvent { e, t -> if (e == null && t == null) view.showUnknownBarcodeMessage() }
            .doOnSuccess { equipmentId = it.id }
            .flatMap {
                if (it.scanState != ScanState.NotScanned) {
                    view.showEquipmentAlreadyScannedMessage()
                    Maybe.empty()
                } else {
                    view.displayProgressBarForEquipment(it.id)
                    if (view.isScrolling.not()) {
                        scrollingValve.onNext(false)
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
            .doOnDispose {
                // Sync in the background if the scanning was interrupted
                syncService.syncEquipments()
                equipmentId?.let { view.hideProgressBarForEquipment(it) }
            }
            .toObservable()
            .compose(ObservableTransformers.valve(scrollingValve.toObservable(), true))
            .delay(1, TimeUnit.SECONDS)
            .applySchedulers(schedulerProvider)
            .subscribe(
                {
                    view.hideProgressBarForEquipment(it.id)
                    if (it.deskId != it.previousDeskId) view.showEquipmentMovedMessage(it.id)
                    view.animateEquipment(it.id)
                },
                { _ ->
                    view.showErrorMessage()
                    equipmentId?.let { view.hideProgressBarForEquipment(it) }
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
                if (view.isScrolling.not()) view.rebuildUi()
                view.displayEquipmentConditionChangedMessage()
            },
                { view.showErrorMessage() })

        disposables.add(disposable)
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

        filterSubject.onNext(storage.equipmentFilter)
    }

    fun stop() {
        disposables.clear()
    }

    companion object {
        private const val EQUIPMENT_BARCODE_LENGTH = 5
    }
}