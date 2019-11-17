package com.example.onmbarcode.presentation.station

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_station)
abstract class StationEpoxyModel : EpoxyModelWithHolder<StationHolder>() {
    @EpoxyAttribute
    lateinit var station: Station

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: StationHolder) {
        super.bind(holder)
        holder.apply {
            title.text = station.title
            scanCount.text =
                view.context.getString(
                    R.string.scanned_desk_count,
                    station.scanCount,
                    station.totalScanCount
                )
            view.setOnClickListener(clickListener)
        }
    }

    override fun unbind(holder: StationHolder) {
        super.unbind(holder)
        holder.view.setOnClickListener(null)
    }
}

class StationHolder : KotlinEpoxyHolder() {
    lateinit var view: View

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
    }

    val title by bind<TextView>(R.id.title)
    val scanCount by bind<TextView>(R.id.scanCount)
}