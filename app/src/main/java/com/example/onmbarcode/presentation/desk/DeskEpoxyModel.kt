package com.example.onmbarcode.presentation.desk

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import kotlinx.android.synthetic.main.item_desk.view.*

@EpoxyModelClass(layout = R.layout.item_desk)
abstract class DeskEpoxyModel : EpoxyModelWithHolder<DeskHolder>() {
    @EpoxyAttribute
    lateinit var desk: DeskUi

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: DeskHolder) {
        super.bind(holder)
        holder.apply {
            deskBarcode.text = "$DESK_BARCODE_PREFIX${desk.barcode}"
            scanCount.text = view.context.getString(
                R.string.scanned_equipment_count,
                desk.scannedEquipmentCount,
                desk.equipmentsCount
            )
            syncedCount.text = view.context.getString(
                R.string.synced_equipment_count,
                desk.syncedEquipmentCount,
                desk.equipmentsCount
            )
            view.setOnClickListener(clickListener)
        }
    }

    override fun unbind(holder: DeskHolder) {
        super.unbind(holder)
        holder.view.setOnClickListener(null)
    }

    companion object {
        private const val DESK_BARCODE_PREFIX = "@"
    }
}

class DeskHolder : KotlinEpoxyHolder() {
    lateinit var view: View

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
    }

    val deskBarcode by bind<TextView>(R.id.deskBarcode)
    val scanCount by bind<TextView>(R.id.scanCount)
    val syncedCount by bind<TextView>(R.id.syncedCount)
}