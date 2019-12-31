package com.example.onmbarcode.presentation.equipment

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

@EpoxyModelClass(layout = R.layout.view_tags)
abstract class EquipmentStatsEpoxyModel : EpoxyModelWithHolder<EquipmentStatsHolder>() {
    @EpoxyAttribute
    lateinit var desk: Desk

    override fun bind(holder: EquipmentStatsHolder) {
        super.bind(holder)
        holder.apply {
            scannedCount.setText((desk.syncedEquipmentCount).toString())
            notSyncedCount.setText((desk.scannedEquipmentCount - desk.syncedEquipmentCount).toString())
            notScannedCount.setText((desk.equipmentCount - desk.scannedEquipmentCount).toString())

            scannedCount.setOnClickListener {
                selectTag(scannedCount, scannedLayout, R.color.scanned_and_synced)
            }
            notSyncedCount.setOnClickListener {
                selectTag(notSyncedCount, notSyncedLayout, R.color.scanned_but_not_synced)
            }
            notScannedCount.setOnClickListener {
                selectTag(notScannedCount, notScannedLayout, R.color.not_scanned)
            }
        }
    }

    private fun selectTag(
        tagInput: TextInputEditText,
        tagLayout: TextInputLayout,
        @ColorRes tagSelectedColor: Int,
        @ColorRes tagUnselectedColor: Int = android.R.color.white
    ) {
        val selectedColor = ContextCompat.getColor(tagInput.context, tagSelectedColor)
        val unselectedColor = ContextCompat.getColor(tagInput.context, tagUnselectedColor)

        @ColorInt val backgroundColor: Int
        @ColorInt val foregroundColor: Int

        if (tagLayout.boxBackgroundColor == unselectedColor || tagLayout.boxBackgroundColor == 0) {
            backgroundColor = selectedColor
            foregroundColor = unselectedColor
        } else {
            backgroundColor = unselectedColor
            foregroundColor = selectedColor
        }

        tagInput.setTextColor(foregroundColor)
        tagLayout.setStartIconTintList(ColorStateList.valueOf(foregroundColor))
        tagLayout.boxBackgroundColor = backgroundColor
    }
}

class EquipmentStatsHolder : KotlinEpoxyHolder() {
    val scannedCount by bind<TextInputEditText>(R.id.scannedCount)
    val scannedLayout by bind<TextInputLayout>(R.id.scannedLayout)
    val notSyncedCount by bind<TextInputEditText>(R.id.notSyncedCount)
    val notSyncedLayout by bind<TextInputLayout>(R.id.notSyncedLayout)
    val notScannedCount by bind<TextInputEditText>(R.id.notScannedCount)
    val notScannedLayout by bind<TextInputLayout>(R.id.notScannedLayout)
}