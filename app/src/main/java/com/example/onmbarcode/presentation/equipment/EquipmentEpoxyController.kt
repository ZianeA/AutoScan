package com.example.onmbarcode.presentation.equipment

import com.airbnb.epoxy.AsyncEpoxyController
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.skeleton.SkeletonTagsHolder
import com.example.onmbarcode.presentation.equipment.skeleton.skeletonEquipment
import com.example.onmbarcode.presentation.equipment.skeleton.skeletonTags

class EquipmentEpoxyController(
    private val dropdownMenuItemSelectedListener: ((conditionIndex: Int, equipment: Equipment) -> Unit),
    private val onTagClickedListener: ((tag: Equipment.ScanState) -> Unit)
) :
    AsyncEpoxyController() {
    lateinit var desk: Desk
    lateinit var selectedTags: Set<String>
    var equipments: List<Equipment> = emptyList()
    var skeletonEquipmentCount = 0

    override fun buildModels() {
        if (skeletonEquipmentCount > 0) {
            skeletonTags { id("st") }

            for (i in 0..skeletonEquipmentCount) {
                skeletonEquipment { id("s", i.toLong()) }
            }

            return
        }

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