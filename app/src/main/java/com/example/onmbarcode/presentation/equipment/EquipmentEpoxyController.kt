package com.example.onmbarcode.presentation.equipment

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class EquipmentEpoxyController(private val onEquipmentClickListener: View.OnClickListener): AsyncEpoxyController() {
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
                .clickListener(onEquipmentClickListener)
                .addTo(this)
        }
    }
}