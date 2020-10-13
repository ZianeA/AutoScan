package com.meteoalgerie.autoscan.desk

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.card.MaterialCardView
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.util.KotlinEpoxyHolder
import com.meteoalgerie.autoscan.common.util.dpToPx
import com.meteoalgerie.autoscan.common.util.getColorFromAttr


@EpoxyModelClass(layout = R.layout.item_desk)
abstract class DeskEpoxyModel : EpoxyModelWithHolder<DeskHolder>() {
    @EpoxyAttribute
    lateinit var desk: Desk

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var menuItemClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var longClickListener: View.OnLongClickListener

    @JvmField
    @EpoxyAttribute
    var isSelected = false

    override fun bind(holder: DeskHolder) {
        super.bind(holder)
        holder.apply {
            val context = itemView.context
            deskBarcode.text = desk.barcode
            equipmentCount.text = itemView.context.getString(
                R.string.equipment_count,
                desk.equipmentCount
            )
            syncedCount.text = desk.syncedEquipmentCount.toString()
            notSyncedCount.text = desk.notSyncedEquipmentCount.toString()
            notScannedCount.text = desk.notScannedEquipmentCount.toString()
            itemView.setOnClickListener(clickListener)
            itemView.setOnLongClickListener(longClickListener)
            itemView.strokeWidth = dpToPx(context, if (isSelected) 1.5f else 0f)
        }
    }

    override fun unbind(holder: DeskHolder) {
        super.unbind(holder)
        holder.apply {
            itemView.setOnClickListener(null)
            itemView.setOnLongClickListener(null)
        }
    }
}

class DeskHolder : KotlinEpoxyHolder() {
    val itemView by bind<MaterialCardView>(R.id.itemRootView)
    val deskBarcode by bind<TextView>(R.id.deskBarcode)
    val syncedCount by bind<TextView>(R.id.syncedCount)
    val notSyncedCount by bind<TextView>(R.id.notSyncedCount)
    val notScannedCount by bind<TextView>(R.id.notScannedCount)
    val equipmentCount by bind<TextView>(R.id.equipmentCount)
}