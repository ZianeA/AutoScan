package com.example.onmbarcode.presentation.equipment

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

//TODO rename listener
class EquipmentEpoxyController(
    private val onEquipmentClickListener: ((
        equipment: Equipment,
        equipmentIndex: Int,
        allEquipments: List<Equipment>
    ) -> Unit)
) : AsyncEpoxyController() {
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
                .clickListener { _ -> onEquipmentClickListener.invoke(it, i, equipments) }
                .addTo(this)
        }
    }
}