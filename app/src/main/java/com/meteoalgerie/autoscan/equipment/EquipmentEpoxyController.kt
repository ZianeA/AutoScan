package com.meteoalgerie.autoscan.equipment

import com.airbnb.epoxy.AsyncEpoxyController
import com.meteoalgerie.autoscan.desk.Desk
import com.meteoalgerie.autoscan.equipment.skeleton.skeletonEquipment
import com.meteoalgerie.autoscan.equipment.skeleton.skeletonTags

class EquipmentEpoxyController(
    private val dropdownMenuItemSelectedListener: ((conditionIndex: Int, equipment: Equipment) -> Unit),
    private val onTagClickedListener: ((tag: Equipment.ScanState) -> Unit)
) :
    AsyncEpoxyController() {
    var desk: Desk? = null
    var selectedTags: Set<String> = emptySet()
    var equipment: List<Equipment> = emptyList()
    var skeletonEquipmentCount = 0

    override fun buildModels() {
        if (skeletonEquipmentCount > 0) {
            skeletonTags { id("st") }

            for (i in 0..skeletonEquipmentCount) {
                skeletonEquipment { id("s", i.toLong()) }
            }

            // When equipment are loading don't show the stats
            return
        }

        desk?.let {
            if(selectedTags.isNotEmpty()) {
                EquipmentStatsEpoxyModel_()
                    .id(it.id)
                    .desk(it)
                    .selectedTags(selectedTags)
                    .onTagClickedListener(onTagClickedListener)
                    .addTo(this)
            }
        }

        equipment.forEach {
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