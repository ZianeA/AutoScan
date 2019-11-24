package com.example.onmbarcode.presentation.equipment

import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class EquipmentPresenter @Inject constructor(
    private val view: EquipmentView,
    private val equipmentRepository: EquipmentRepository,
    private val schedulerProvider: SchedulerProvider
) {
    private val disposables = CompositeDisposable()

    private lateinit var currentEquipments: MutableList<Equipment>
    private lateinit var scannedEquipment: Equipment
    private var hasScanned = false

    fun start(desk: Desk) {
        val disposable = equipmentRepository.getEquipments(desk.barcode)
            .map { equipments -> equipments.sortedByDescending { it.scanDate } }
            .applySchedulers(schedulerProvider)
            .doOnSuccess { currentEquipments = it.toMutableList() }
            .subscribe({ view.displayEquipments(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    //TODO do rename
    private fun scanBarcode(barcode: String) {
        val parsedBarcode = barcode.toIntOrNull()
            ?: throw IllegalArgumentException(
                "Malformed equipment barcode. Equipment barcode must contain only numeric values"
            )
        view.clearBarcodeInputArea()

        val disposable = equipmentRepository.findEquipment(parsedBarcode)
            .doOnSuccess { scannedEquipment = it }
            .flatMap {
                Single.fromCallable {
                    val scannedEquipmentIndex = currentEquipments.indexOf(scannedEquipment)
                    scannedEquipment = scannedEquipment.copy(
                        scanState = ScanState.PendingScan
                    ) //TODO refactor, duplicate code
                    currentEquipments.apply {
                        removeAt(scannedEquipmentIndex)
                        add(0, scannedEquipment)
                    }
                }
            }
            .observeOn(schedulerProvider.main)
            .doOnSuccess {
                currentEquipments = it
                hasScanned = true
                view.smoothScrollToTop()
            }
            .observeOn(schedulerProvider.worker)
            .flatMapCompletable {
                scannedEquipment = scannedEquipment.copy(
                    scanState = ScanState.ScannedAndSynced,
                    scanDate = System.currentTimeMillis()
                )

                equipmentRepository.updateEquipment(scannedEquipment)
            }/*.delay(20, TimeUnit.MILLISECONDS) //TODO remove this delay*/
            .doOnComplete { currentEquipments[0] = scannedEquipment }
            .applySchedulers(schedulerProvider)
            .subscribe(
                {
                    hasScanned = true
                    view.smoothScrollToTop()
                },
                { /*onError*/ })

        disposables.add(disposable)
    }

    fun onBarcodeChange(barcode: String) {
        when {
            barcode.length < 5 -> return
            barcode.length == EQUIPMENT_BARCODE_LENGTH -> scanBarcode(barcode)
            else -> throw IllegalArgumentException("Equipment barcode must not be more than 5 digits long")
        }
    }

    fun onEquipmentConditionPicked(conditionIndex: Int) {
        // TODO("Not implemented")
    }

    fun onSmoothScrollToTopEnd() {
        view.displayEquipments(currentEquipments, scannedEquipment.barcode)
    }

    fun onEquipmentsDisplayed() {
        if (hasScanned.not()) return

        view.scrollToTop()
        hasScanned = false
    }

    fun onEquipmentAnimationEnd() {
    }

    fun stop() {
        disposables.clear()
    }

    companion object {
        private const val EQUIPMENT_BARCODE_LENGTH = 5
    }
}