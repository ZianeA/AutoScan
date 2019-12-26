package com.example.onmbarcode.presentation.equipment

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import com.google.android.material.textfield.TextInputEditText

@EpoxyModelClass(layout = R.layout.view_equipment_stats)
abstract class EquipmentStatsEpoxyModel : EpoxyModelWithHolder<EquipmentStatsHolder>() {
    override fun bind(holder: EquipmentStatsHolder) {
        super.bind(holder)
        holder.apply {

        }
    }
}

class EquipmentStatsHolder : KotlinEpoxyHolder() {
    val notScannedCount by bind<TextInputEditText>(R.id.notScannedCount)
    val notSyncedCount by bind<TextInputEditText>(R.id.notSyncedCount)
}