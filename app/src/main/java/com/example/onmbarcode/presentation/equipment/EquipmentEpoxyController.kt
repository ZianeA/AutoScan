package com.example.onmbarcode.presentation.equipment

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

//TODO rename listener
class EquipmentEpoxyController : AsyncEpoxyController() {
    var equipments: List<Equipment> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        equipments.forEachIndexed { i, it ->
            EquipmentEpoxyModel_()
                .id(it.barcode)
                .equipment(it)
                .addTo(this)
        }
    }
}