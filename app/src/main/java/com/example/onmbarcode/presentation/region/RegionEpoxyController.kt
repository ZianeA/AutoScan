package com.example.onmbarcode.presentation.region

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class RegionEpoxyController(private val onRegionClickListener: ((clickedRegion: Region) -> Unit)) :
    AsyncEpoxyController() {
    override fun buildModels() {
        region {
            val darElBeidaRegion = Region("Dar El Beida", 20, 40)
            id(0)
            region(darElBeidaRegion)
            clickListener(View.OnClickListener { onRegionClickListener.invoke(darElBeidaRegion) })
        }

        region {
            val babEzzouarRegion = Region("Bab Ezzouar", 32, 50)
            id(1)
            region(babEzzouarRegion)
            clickListener { _ -> onRegionClickListener.invoke(babEzzouarRegion) }
        }
    }
}