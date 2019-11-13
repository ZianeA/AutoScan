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

    override fun bind(holder: DeskHolder) {
        super.bind(holder)
        holder.apply {
            deskBarcode.text = view.context.getString(R.string.desk_barcode, desk.barcode)
            scanCount.text = view.context.getString(
                R.string.scanned_equipment_count,
                desk.scanCount,
                desk.totalScanCount
            )
        }
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
}