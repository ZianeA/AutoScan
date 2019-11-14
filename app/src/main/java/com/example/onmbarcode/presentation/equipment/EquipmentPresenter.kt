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

    fun start(desk: Desk) {
        val disposable = equipmentRepository.getEquipments(desk.barcode)
            .applySchedulers(schedulerProvider)
            .subscribe({ view.displayEquipments(it) }, { /*onError*/ })

        disposables.add(disposable)
    }

    fun onEquipmentScanned(
        scannedEquipment: Equipment,
        equipmentIndex: Int,
        allEquipment: List<Equipment>
    ) {
        val rearrangedEquipmentList = allEquipment.toMutableList()
            .apply {
                removeAt(equipmentIndex)
                add(0, scannedEquipment.copy(isScanned = true))
            }
            .toList()

        view.displayEquipments(rearrangedEquipmentList)
    }

    fun stop() {
        disposables.clear()
    }
}