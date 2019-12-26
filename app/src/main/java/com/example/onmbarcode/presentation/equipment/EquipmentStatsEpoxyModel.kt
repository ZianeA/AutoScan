package com.example.onmbarcode.presentation.equipment

import android.text.Editable
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import com.google.android.material.textfield.TextInputEditText

@EpoxyModelClass(layout = R.layout.view_equipment_stats)
abstract class EquipmentStatsEpoxyModel : EpoxyModelWithHolder<EquipmentStatsHolder>() {
    
    @EpoxyAttribute
    lateinit var desk: Desk

    override fun bind(holder: EquipmentStatsHolder) {
        super.bind(holder)
        holder.apply {
            notScannedCount.setText(desk.equipmentCount - desk.scannedEquipmentCount)
            notSyncedCount.setText(desk.equipmentCount - desk.syncedEquipmentCount)
        }
    }
}

class EquipmentStatsHolder : KotlinEpoxyHolder() {
    val notScannedCount by bind<TextInputEditText>(R.id.notScannedCount)
    val notSyncedCount by bind<TextInputEditText>(R.id.notSyncedCount)
}