package com.example.onmbarcode.presentation.equipment

import com.example.onmbarcode.data.equipment.EquipmentRepository
import com.example.onmbarcode.presentation.desk.DeskUi
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.Clock
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException
import java.lang.IllegalArgumentException
import javax.inject.Inject

@FragmentScope
class EquipmentPresenter @Inject constructor(
    private val view: EquipmentView,
    private val equipmentRepository: EquipmentRepository,
    private val schedulerProvider: SchedulerProvider,
    private val clock: Clock
) {
    private val disposables = CompositeDisposable()

    fun start(desk: DeskUi) {
        val disposable = equipmentRepository.getEquipments(desk.id)
            .map { equipments -> equipments.sortedByDescending { it.scanDate } }
            .applySchedulers(schedulerProvider)
            .subscribe({
                view.equipments = it
                view.displayEquipments()
            }, { /*onError*/ })

        disposables.add(disposable)
    }

    //TODO handle equipment not found
    private fun scanBarcode(barcode: String) {
        // TODO i'm not sure if this works with a barcode 00001
        barcode.toIntOrNull()
            ?: throw IllegalArgumentException(
                "Malformed equipment barcode. Equipment barcode must contain only numeric values"
            )
        view.clearBarcodeInputArea()

        val disposable = equipmentRepository.findEquipment(barcode)
            .observeOn(schedulerProvider.main)
            .map {
                object {
                    val scannedEquipment = it
                    val equipments = view.equipments.toMutableList()
                }
            }
            .observeOn(schedulerProvider.worker)
            .map {
                val scannedEquipmentIndex =
                    it.equipments.indexOfFirst { e -> e.barcode == it.scannedEquipment.barcode }
                it.equipments.apply {
                    removeAt(scannedEquipmentIndex)
                    add(0, it.scannedEquipment.copy(scanState = ScanState.PendingScan))
                }

                it
            }
            .observeOn(schedulerProvider.main)
            .doOnSuccess {
                view.equipments = it.equipments
                view.scrollToTopAndDisplayEquipments()
            }
            .observeOn(schedulerProvider.worker)
            .flatMap { holder ->
                val updatedEquipment =
                    holder.scannedEquipment.copy( //TODO be careful with equipment scan state
                        scanState = ScanState.ScannedAndSynced,
                        scanDate = clock.currentTimeSeconds
                    )
                equipmentRepository.updateEquipment(updatedEquipment)
                    .andThen(Single.just(updatedEquipment))
                    //TODO update errors since we are not using Retrofit anymore
                    .onErrorResumeNext {
                        when (it) {
                            is IOException -> {
                                //handle network related errors
                                val scannedButNotSyncedEquipment =
                                    updatedEquipment.copy(scanState = ScanState.ScannedButNotSynced)
                                Single.just(scannedButNotSyncedEquipment)
                            }
                            else -> {
                                // This is probably a serious error
                                Single.error(it)
                            }
                        }
                    }
            }/*.delay(
                Random.nextLong(200, 1000),
                TimeUnit.MILLISECONDS,
                schedulerProvider.worker
            ) //TODO remove this delay*/
            .observeOn(schedulerProvider.main)
            .map {
                object {
                    val updatedEquipment = it
                    // Get fresh equipments.
                    // Remember this runs asynchronously, equipments could have changed
                    val equipments = view.equipments
                }
            }
            .observeOn(schedulerProvider.worker)
            .map {
                val equipments = it.equipments.toMutableList()
                val scannedEquipmentIndex =
                    equipments.indexOfFirst { e -> e.barcode == it.updatedEquipment.barcode }
                equipments[scannedEquipmentIndex] = it.updatedEquipment
                object {
                    val barcode = it.updatedEquipment.barcode;
                    val equipments = equipments
                }
            }
            .applySchedulers(schedulerProvider)
            .subscribe(
                {
                    view.equipments = it.equipments
                    view.equipmentToAnimate = it.barcode
                    view.displayEquipmentsDelayed()
                }, { view.showErrorMessage() })

        disposables.add(disposable)
    }

    fun onBarcodeChange(barcode: String) {
        when {
            barcode.length < 5 -> return
            barcode.length == EQUIPMENT_BARCODE_LENGTH -> scanBarcode(barcode)
            else -> throw IllegalArgumentException("Equipment barcode must not be more than 5 digits long")
        }
    }

    fun onEquipmentConditionPicked(conditionIndex: Int, equipment: Equipment) {
        val disposable = equipmentRepository.updateEquipment(
            equipment.copy(
                condition = EquipmentCondition.getByValue(conditionIndex)
            )
        ).applySchedulers(schedulerProvider)
            .subscribe({ view.displayEquipmentConditionChangedMessage() }, {})

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }

    companion object {
        private const val EQUIPMENT_BARCODE_LENGTH = 5
    }
}