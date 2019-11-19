package com.example.onmbarcode.presentation.equipment

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class EquipmentEpoxyController : AsyncEpoxyController() {
    var equipments: List<Equipment> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    var equipmentToAnimateBarcode: Int = -1

    override fun buildModels() {
        equipments.forEach {
            EquipmentEpoxyModel_()
                .id(it.barcode)
                .equipment(it)
                .controller(this)
                .addTo(this)
        }
    }
}