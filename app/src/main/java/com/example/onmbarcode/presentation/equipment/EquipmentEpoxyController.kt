package com.example.onmbarcode.presentation.equipment

import com.airbnb.epoxy.AsyncEpoxyController
import com.example.onmbarcode.presentation.desk.Desk

class EquipmentEpoxyController(
    private val dropdownMenuItemSelectedListener: ((conditionIndex: Int, equipment: Equipment) -> Unit),
    private val onTagClickedListener: ((tag: Equipment.ScanState) -> Unit)
) :
    AsyncEpoxyController() {
    lateinit var desk: Desk
    lateinit var selectedTags: Set<String>
    var equipments: List<Equipment> = emptyList()

    override fun buildModels() {
        EquipmentStatsEpoxyModel_()
            .id(desk.id)
            .desk(desk)
            .selectedTags(selectedTags)
            .onTagClickedListener(onTagClickedListener)
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