package com.meteoalgerie.autoscan.common.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private val itemSpacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = itemSpacing
        }
        outRect.bottom = itemSpacing
        outRect.left = itemSpacing
        outRect.right = itemSpacing
    }
}