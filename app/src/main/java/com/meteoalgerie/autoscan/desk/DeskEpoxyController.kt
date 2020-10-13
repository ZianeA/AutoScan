package com.meteoalgerie.autoscan.desk

import android.view.View
import com.airbnb.epoxy.AsyncEpoxyController

class DeskEpoxyController : AsyncEpoxyController() {
    var onDeskClickListener: ((clickedDesk: Desk) -> Unit)? = null
    var onDeskLongClickListener: (() -> Unit)? = null

    var desks: List<Desk> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    val selectedDesks = mutableSetOf<Desk>()

    override fun buildModels() {
        desks.forEach {
            DeskEpoxyModel_()
                .id(it.barcode)
                .desk(it)
                .isSelected(selectedDesks.contains(it))
                .clickListener { _ -> onDeskClickListener?.invoke(it) }
                .longClickListener { _ -> onDeskLongClicked(it) }
                .addTo(this)
        }
    }

    private fun onDeskLongClicked(desk: Desk): Boolean {
        if (selectedDesks.contains(desk)) {
            selectedDesks.remove(desk)
        } else {
            selectedDesks.add(desk)
        }
        onDeskLongClickListener?.invoke()
        requestModelBuild()
        return true
    }
}