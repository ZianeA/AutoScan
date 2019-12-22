package com.example.onmbarcode.presentation.equipment

import com.example.onmbarcode.data.equipment.EquipmentRepository
import com.example.onmbarcode.presentation.desk.DeskUi
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.Clock
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import com.example.onmbarcode.service.SyncBackgroundService
import de.timroes.axmlrpc.XMLRPCException
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

    fun start(desk: DeskUi) {
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

    // TODO fix bug: when an already scanned equipment is scanned, scroll to the top does not work.
    // TODO add more unit tests, notably for error messages
    private fun scanBarcode(barcode: String, deskId: Int) {
        // TODO i'm not sure if this works with a barcode 00001
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
                    .andThen(Maybe.just(updatedEquipment.id))
                    .onErrorResumeNext { it: Throwable ->
                        when (it) {
                            is XMLRPCException -> {
                                //TODO should probably do this only if it's a no internet connexion exception
                                syncService.syncEquipments()
                                Maybe.just(updatedEquipment.id)
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
                    view.hideProgressBarForEquipment(it)
                    view.animateEquipment(it)
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
        ).applySchedulers(schedulerProvider)
            .subscribe({
                view.hideProgressBarForEquipment(equipment.id)
                view.displayEquipmentConditionChangedMessage()
            }, {})

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }

    companion object {
        private const val EQUIPMENT_BARCODE_LENGTH = 5
    }
}