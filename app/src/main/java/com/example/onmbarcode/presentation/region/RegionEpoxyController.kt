package com.example.onmbarcode.presentation.region

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class RegionEpoxyController(private val onRegionClickListener: ((clickedRegion: Region) -> Unit)) :
    AsyncEpoxyController() {
    override fun buildModels() {
        region {
            val drmcRegion = Region("DRMC", 2, 8)
            id(0)
            region(drmcRegion)
            clickListener(View.OnClickListener { onRegionClickListener.invoke(drmcRegion) })
        }

        region {
            val drmeRegion = Region("DRME", 4, 6)
            id(1)
            region(drmeRegion)
            clickListener { _ -> onRegionClickListener.invoke(drmeRegion) }
        }
    }
}