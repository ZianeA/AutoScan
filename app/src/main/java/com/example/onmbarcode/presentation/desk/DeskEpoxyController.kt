package com.example.onmbarcode.presentation.desk

import com.airbnb.epoxy.AsyncEpoxyController

class DeskEpoxyController : AsyncEpoxyController() {
    var desks: List<Desk> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        desks.forEach {
            DeskEpoxyModel_()
                .id(it.barcode)
                .desk(it)
                .addTo(this)
        }
    }
}