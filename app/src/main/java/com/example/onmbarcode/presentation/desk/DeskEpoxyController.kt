package com.example.onmbarcode.presentation.desk

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class DeskEpoxyController(
    private val onDeskClickListener: ((clickedDesk: Desk) -> Unit),
    private val menuItemClickListener: ((desk: Desk, selectedMenuItem: View) -> Unit)
) : AsyncEpoxyController(
) {
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
                .deskClickListener { _ -> onDeskClickListener.invoke(it) }
                .menuItemClickListener { view -> menuItemClickListener.invoke(it, view) }
                .addTo(this)
        }
    }
}