package com.example.onmbarcode.presentation.desk

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class DeskEpoxyController(private val onDeskClickListener: ((clickedDesk: Desk) -> Unit)) :
    AsyncEpoxyController() {
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
                .clickListener { _ -> onDeskClickListener.invoke(it) }
                .addTo(this)
        }
    }
}