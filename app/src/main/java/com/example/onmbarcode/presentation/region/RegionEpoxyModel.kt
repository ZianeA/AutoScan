package com.example.onmbarcode.presentation.region

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

    override fun bind(holder: RegionHolder) {
        super.bind(holder)
        holder.apply {
            title.text = region.title
            scanCount.text =
                scanCount.context.getString(
                    R.string.scanned_desk_count,
                    region.scanCount,
                    region.maxScanCount
                )
        }
    }

    override fun unbind(holder: RegionHolder) {
        super.unbind(holder)
    }
}

class RegionHolder : KotlinEpoxyHolder() {
    val title by bind<TextView>(R.id.title)
    val scanCount by bind<TextView>(R.id.scanCount)
}