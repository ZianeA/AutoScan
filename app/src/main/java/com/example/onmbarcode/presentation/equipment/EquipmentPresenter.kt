package com.example.onmbarcode.presentation.equipment

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
import io.reactivex.disposables.CompositeDisposable
import java.lang.IllegalArgumentException
import javax.inject.Inject

@FragmentScope
class EquipmentPresenter @Inject constructor(
    private val view: EquipmentView,
    private val equipmentRepository: EquipmentRepository,
    private val syncService: SyncBackgroundService,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()

    fun start(desk: Desk) {
        val disposable = equipmentRepository.getAllEquipmentForDesk(desk.id)
            .map { equipments -> equipments.sortedByDescending { it.scanDate } }
            .applySchedulers(schedulerProvider)
            .subscribe({ if (view.isScrolling.not()) view.displayEquipments(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    fun onScrollEnded(deskId: Int) {
        val disposable = equipmentRepository.getAllEquipmentForDesk(deskId)
            .first(emptyList()) //Unsubscribe after the first emitted item
            .map { equipments -> equipments.sortedByDescending { it.scanDate } }
            .applySchedulers(schedulerProvider)
            .subscribe({ if (view.isScrolling.not()) view.displayEquipments(it) }, { /*onError*/ })

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
            .applySchedulers(schedulerProvider)
            .subscribe({
                view.hideProgressBarForEquipment(equipment.id)
                view.displayEquipmentConditionChangedMessage()
            },
                { view.showErrorMessage() })

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }

    companion object {
        private const val EQUIPMENT_BARCODE_LENGTH = 5
    }
}