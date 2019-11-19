package com.example.onmbarcode.presentation.equipment

import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class EquipmentPresenter @Inject constructor(
    private val view: EquipmentView,
    private val equipmentRepository: EquipmentRepository,
    private val schedulerProvider: SchedulerProvider
) {
    private val disposables = CompositeDisposable()

    private lateinit var scannedEquipment: Equipment
    private var hasScanned = false

    fun start(desk: Desk) {
        val disposable = equipmentRepository.getEquipments(desk.barcode)
            .map { equipments -> equipments.sortedByDescending { it.scanDate } }
            .applySchedulers(schedulerProvider)
            .subscribe(
                { view.displayEquipments(it) },
                { /*onError*/ })

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
        val updatedEquipment = scannedEquipment.copy(
            isScanned = true,
            state = Equipment.EquipmentState.getByValue(stateIndex),
            scanDate = System.currentTimeMillis()
        )
        val disposable = equipmentRepository.updateEquipment(updatedEquipment)
            .applySchedulers(schedulerProvider)
            .doOnComplete { hasScanned = true }
            .subscribe({ view.smoothScrollToTop() }, { /*onError*/ })

        disposables.add(disposable)

        /*val rearrangedEquipmentList = allEquipments.toMutableList()
            .apply {
                removeAt(scannedEquipmentIndex)
                add(
                    0,
                    scannedEquipment.copy(
                        isScanned = true,
                        state = Equipment.EquipmentState.getByValue(stateIndex)
                    )
                )
            }
            .toList()

        allEquipments = rearrangedEquipmentList //TODO refactor.
        hasScanned = true
        view.smoothScrollToTop()*/
    }

    fun onSmoothScrollToTopEnd() {
        val disposable = equipmentRepository.getEquipments(-1)
            .map { equipments -> equipments.sortedByDescending { it.scanDate } }
            .applySchedulers(schedulerProvider)
            .subscribe({ view.displayEquipments(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    fun onEquipmentsDisplayed() {
        if (hasScanned.not()) return

        view.scrollToTop()
//        view.animateEquipment(scannedEquipment.barcode.toLong())
        hasScanned = false
    }

    fun onEquipmentAnimationEnd() {

    }

    fun stop() {
        disposables.clear()
    }
}