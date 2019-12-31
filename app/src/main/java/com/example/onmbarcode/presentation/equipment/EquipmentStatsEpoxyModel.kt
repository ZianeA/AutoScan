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
            notScannedCount.setText((desk.equipmentCount - desk.scannedEquipmentCount).toString())
            notSyncedCount.setText((desk.scannedEquipmentCount - desk.syncedEquipmentCount).toString())
            scanned.setText((desk.syncedEquipmentCount).toString())
        }
    }
}

class EquipmentStatsHolder : KotlinEpoxyHolder() {
    val notScannedCount by bind<TextInputEditText>(R.id.notScannedCount)
    val notSyncedCount by bind<TextInputEditText>(R.id.notSyncedCount)
    val scanned by bind<TextInputEditText>(R.id.scannedCount)
}