package com.example.onmbarcode.presentation.equipment

import android.view.View
import android.widget.AdapterView
import com.airbnb.epoxy.AsyncEpoxyController
import com.example.onmbarcode.presentation.desk.Desk

class EquipmentEpoxyController(private val dropdownMenuItemSelectedListener: ((conditionIndex: Int, equipment: Equipment) -> Unit)) :
    AsyncEpoxyController() {
    lateinit var desk: Desk
    var equipments: List<Equipment> = emptyList()

    override fun buildModels() {
        EquipmentStatsEpoxyModel_()
            .id(desk.id)
            .desk(desk)
            .addTo(this)

        equipments.forEach {
            EquipmentEpoxyModel_()
                .id(it.barcode)
                .equipment(it)
                .dropdownMenuItemClickListener { _, _, position, _ ->
                    dropdownMenuItemSelectedListener.invoke(position, it)
                }
                .addTo(this)
        }
    }
}