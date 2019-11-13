package com.example.onmbarcode.presentation.region

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_region)
abstract class RegionEpoxyModel : EpoxyModelWithHolder<RegionHolder>() {
    @EpoxyAttribute
    lateinit var region: Region

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: RegionHolder) {
        super.bind(holder)
        holder.apply {
            title.text = region.title
            scanCount.text =
                view.context.getString(
                    R.string.scanned_desk_count,
                    region.scanCount,
                    region.totalScanCount
                )
            view.setOnClickListener(clickListener)
        }
    }

    override fun unbind(holder: RegionHolder) {
        super.unbind(holder)
        holder.view.setOnClickListener(null)
    }
}

class RegionHolder : KotlinEpoxyHolder() {
    lateinit var view: View

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
    }

    val title by bind<TextView>(R.id.title)
    val scanCount by bind<TextView>(R.id.scanCount)
}