package com.example.onmbarcode.presentation.desk

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_desk)
abstract class DeskEpoxyModel : EpoxyModelWithHolder<DeskHolder>() {
    @EpoxyAttribute
    lateinit var desk: Desk

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: DeskHolder) {
        super.bind(holder)
        holder.apply {
            deskBarcode.text = desk.barcode
            equipmentCount.text = view.context.getString(
                R.string.equipment_count,
                desk.equipmentCount
            )
            syncedCount.text = desk.syncedEquipmentCount.toString()
            notSyncedCount.text = desk.notSyncedEquipmentCount.toString()
            notScannedCount.text = desk.notScannedEquipmentCount.toString()
            view.setOnClickListener(clickListener)
        }
    }

    override fun unbind(holder: DeskHolder) {
        super.unbind(holder)
        holder.view.setOnClickListener(null)
    }
}

class DeskHolder : KotlinEpoxyHolder() {
    lateinit var view: View

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
    }

    val deskBarcode by bind<TextView>(R.id.deskBarcode)
    val syncedCount by bind<TextView>(R.id.syncedCount)
    val notSyncedCount by bind<TextView>(R.id.notSyncedCount)
    val notScannedCount by bind<TextView>(R.id.notScannedCount)
    val equipmentCount by bind<TextView>(R.id.equipmentCount)
}