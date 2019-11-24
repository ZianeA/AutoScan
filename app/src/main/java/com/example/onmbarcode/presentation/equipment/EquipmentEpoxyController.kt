package com.example.onmbarcode.presentation.equipment

import android.view.View
import android.widget.AdapterView
import com.airbnb.epoxy.AsyncEpoxyController

class EquipmentEpoxyController(private val dropdownMenuItemSelectedListener: ((conditionIndex: Int, equipment: Equipment) -> Unit)) :
    AsyncEpoxyController() {
    var equipments: List<Equipment> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        equipments.forEach {
            EquipmentEpoxyModel_()
                .id(it.barcode)
                .equipment(it)
                .dropdownMenuItemClickListener(AdapterView.OnItemClickListener { _, _, position, _ ->
                    dropdownMenuItemSelectedListener.invoke(position, it)
                })
                .addTo(this)
        }
    }
}