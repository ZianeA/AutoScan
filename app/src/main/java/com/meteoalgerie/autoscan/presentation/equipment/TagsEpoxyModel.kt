package com.meteoalgerie.autoscan.presentation.equipment

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.presentation.desk.Desk
import com.meteoalgerie.autoscan.presentation.util.KotlinEpoxyHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.meteoalgerie.autoscan.data.equipment.Equipment.*
import com.meteoalgerie.autoscan.presentation.util.getColorFromAttr

@EpoxyModelClass(layout = R.layout.view_tags)
abstract class EquipmentStatsEpoxyModel : EpoxyModelWithHolder<EquipmentStatsHolder>() {
    @EpoxyAttribute
    lateinit var desk: Desk

    @EpoxyAttribute
    lateinit var selectedTags: Set<String>

    @EpoxyAttribute
    lateinit var onTagClickedListener: ((tag: ScanState) -> Unit)

    override fun bind(holder: EquipmentStatsHolder) {
        super.bind(holder)
        holder.apply {
            syncedCount.setText((desk.syncedEquipmentCount).toString())
            notSyncedCount.setText((desk.notSyncedEquipmentCount).toString())
            notScannedCount.setText((desk.notScannedEquipmentCount).toString())

            deselectAllTags(this)
            selectedTags.forEach {
                when (it) {
                    ScanState.ScannedAndSynced.name -> {
                        selectTag(context, syncedCount, syncedLayout, R.color.scanned_and_synced)
                    }
                    ScanState.ScannedButNotSynced.name -> {
                        selectTag(
                            context, notSyncedCount, notSyncedLayout, R.color.scanned_but_not_synced
                        )
                    }
                    ScanState.NotScanned.name -> {
                        selectTag(context, notScannedCount, notScannedLayout, R.color.not_scanned)
                    }
                }
            }

            syncedCount.setOnClickListener { onTagClickedListener(ScanState.ScannedAndSynced) }
            notSyncedCount.setOnClickListener { onTagClickedListener(ScanState.ScannedButNotSynced) }
            notScannedCount.setOnClickListener { onTagClickedListener(ScanState.NotScanned) }
        }
    }

    override fun unbind(holder: EquipmentStatsHolder) {
        super.unbind(holder)
        deselectAllTags(holder)
    }

    private fun selectTag(
        context: Context,
        tagInput: TextInputEditText,
        tagLayout: TextInputLayout,
        @ColorRes backgroundColor: Int
    ) {
        val foregroundColor = ContextCompat.getColor(context, R.color.white)

        tagInput.setTextColor(foregroundColor)
        tagLayout.setStartIconTintList(ColorStateList.valueOf(foregroundColor))
        tagLayout.boxBackgroundColor = ContextCompat.getColor(context, backgroundColor)
    }

    private fun deselectTag(
        context: Context,
        tagInput: TextInputEditText,
        tagLayout: TextInputLayout,
        @ColorRes foregroundColorRes: Int
    ) {
        val foregroundColor = ContextCompat.getColor(context, foregroundColorRes)

        tagInput.setTextColor(foregroundColor)
        tagLayout.setStartIconTintList(ColorStateList.valueOf(foregroundColor))
        tagLayout.boxBackgroundColor = context.getColorFromAttr(R.attr.colorSurface)
    }

    private fun deselectAllTags(holder: EquipmentStatsHolder) {
        holder.apply {
            deselectTag(context, syncedCount, syncedLayout, R.color.scanned_and_synced)
            deselectTag(context, notSyncedCount, notSyncedLayout, R.color.scanned_but_not_synced)
            deselectTag(context, notScannedCount, notScannedLayout, R.color.not_scanned)
        }
    }
}

class EquipmentStatsHolder : KotlinEpoxyHolder() {
    val syncedCount by bind<TextInputEditText>(R.id.syncedCount)
    val syncedLayout by bind<TextInputLayout>(R.id.syncedLayout)
    val notSyncedCount by bind<TextInputEditText>(R.id.notSyncedCount)
    val notSyncedLayout by bind<TextInputLayout>(R.id.notSyncedLayout)
    val notScannedCount by bind<TextInputEditText>(R.id.notScannedCount)
    val notScannedLayout by bind<TextInputLayout>(R.id.notScannedLayout)

    lateinit var context: Context

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        context = itemView.context
    }
}