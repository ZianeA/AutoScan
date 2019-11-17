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

    fun start(desk: Desk) {
        val disposable = equipmentRepository.getEquipments(desk.barcode)
            .applySchedulers(schedulerProvider)
            .subscribe({ view.displayEquipments(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    fun onEquipmentScanned(
        scannedEquipment: Equipment,
        scannedEquipmentIndex: Int,
        allEquipments: List<Equipment>
    ) {
        this.scannedEquipment = scannedEquipment
        this.scannedEquipmentIndex = scannedEquipmentIndex
        this.allEquipments = allEquipments
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

        view.displayEquipments(rearrangedEquipmentList)
        view.scrollToTop()
    }

    fun stop() {
        disposables.clear()
    }
}