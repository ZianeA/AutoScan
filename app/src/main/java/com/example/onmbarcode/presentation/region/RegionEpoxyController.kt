package com.example.onmbarcode.presentation.region

import com.airbnb.epoxy.AsyncEpoxyController

class RegionEpoxyController : AsyncEpoxyController() {
    override fun buildModels() {
        region {
            id(0)
            region(Region("Dar El Beida", 20, 40))
        }
        region {
            id(1)
            region(Region("Bab Ezzouar", 32, 50))
        }
    }
}