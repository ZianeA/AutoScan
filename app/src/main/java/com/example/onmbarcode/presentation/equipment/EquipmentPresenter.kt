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

    // TODO This is used for testing purposes only, should be removed
    private lateinit var allEquipments: List<Equipment>
    private lateinit var scannedEquipment: Equipment
    private var scannedEquipmentIndex: Int = -1
    private var hasScanned = false

    fun start(desk: Desk) {
        val disposable = equipmentRepository.getEquipments(desk.barcode)
            .applySchedulers(schedulerProvider)
            .subscribe(
                { view.displayEquipments(it); /*TODO remove*/ allEquipments = it },
                { /*onError*/ })

        disposables.add(disposable)
    }

    fun onBarcodeEntered(barcode: String) {
        val scannedEquipment =
            allEquipments.filterIndexed { index, it ->
                if (it.barcode == barcode.toInt()) {
                    scannedEquipmentIndex = index
                    true
                } else {
                    false
                }
            }
                .first()

        this.scannedEquipment = scannedEquipment
        view.displayEquipmentStatePicker(scannedEquipment.state)
    }

    fun onEquipmentStatePicked(stateIndex: Int) {
        val rearrangedEquipmentList = allEquipments.toMutableList()
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
        view.smoothScrollToTop()
    }

    fun onSmoothScrollToTopEnd() {
        view.displayEquipments(allEquipments)
    }

    fun onEquipmentsDisplayed() {
        if (!hasScanned) return

        view.scrollToTop()
        view.animateEquipment(scannedEquipment.barcode.toLong())
        hasScanned = false
    }

    fun onEquipmentAnimationEnd() {

    }

    fun stop() {
        disposables.clear()
    }
}