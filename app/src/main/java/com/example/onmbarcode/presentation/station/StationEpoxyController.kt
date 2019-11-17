package com.example.onmbarcode.presentation.station

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class StationEpoxyController(private val onStationClickListener: ((clickedStation: Station) -> Unit)) :
    AsyncEpoxyController() {
    override fun buildModels() {
        station {
            val darElBeidaStation = Station("DAR-EL-BEIDA-HB", 20, 40)
            id(0)
            station(darElBeidaStation)
            clickListener(View.OnClickListener { onStationClickListener.invoke(darElBeidaStation) })
        }

        station {
            val algerPortStation = Station("ALGER PORT", 32, 50)
            id(1)
            station(algerPortStation)
            clickListener { _ -> onStationClickListener.invoke(algerPortStation) }
        }
    }
}