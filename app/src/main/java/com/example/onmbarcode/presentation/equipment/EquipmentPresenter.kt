package com.example.onmbarcode.presentation.equipment

import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
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

    //TODO do asynchronously
    fun onBarcodeEntered(barcode: String) {
        val parsedBarcode = barcode.toInt() //TODO add sanity check
        val disposable = equipmentRepository.findEquipment(parsedBarcode)
            .applySchedulers(schedulerProvider)
            .doOnSuccess { scannedEquipment = it }
            .subscribe({ view.displayEquipmentStatePicker(it.state) }, { /*onError*/ })

        disposables.add(disposable)
    }

    fun onEquipmentStatePicked(stateIndex: Int) {
        // Update UI first and then update database and server
        val disposable = Single.fromCallable {
            val scannedEquipmentIndex = currentEquipments.indexOf(scannedEquipment)
            scannedEquipment = scannedEquipment.copy(
                scanState = ScanState.PendingScan,
                state = EquipmentState.getByValue(stateIndex)
            ) //TODO refactor, duplicate code
            currentEquipments.apply {
                removeAt(scannedEquipmentIndex)
                add(0, scannedEquipment)
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
                    state = EquipmentState.getByValue(stateIndex),
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

    fun onSmoothScrollToTopEnd() {
        view.displayEquipments(currentEquipments, scannedEquipment.barcode)
    }

    fun onEquipmentsDisplayed() {
        if (hasScanned.not()) return

        view.scrollToTop()
        hasScanned = false
    }

    fun onEquipmentAnimationEnd() {
//        view.displayEquipments(currentEquipments)
    }

    fun stop() {
        disposables.clear()
    }
}