package com.example.onmbarcode.presentation.desk

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder


@EpoxyModelClass(layout = R.layout.item_desk)
abstract class DeskEpoxyModel : EpoxyModelWithHolder<DeskHolder>() {
    @EpoxyAttribute
    lateinit var desk: Desk

    @EpoxyAttribute
    lateinit var deskClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var menuItemClickListener: View.OnClickListener

    override fun bind(holder: DeskHolder) {
        super.bind(holder)
        holder.apply {
            deskBarcode.text = desk.barcode
            equipmentCount.text = view.context.getString(
                R.string.equipment_count,
                desk.equipmentCount
            )
            syncedCount.text = desk.syncedEquipmentCount.toString()
            notSyncedCount.text = desk.notSyncedEquipmentCount.toString()
            notScannedCount.text = desk.notScannedEquipmentCount.toString()
            view.setOnClickListener(deskClickListener)

            moreButton.setOnClickListener {
                PopupWindow(view.context).apply {
                    val popupLayout = LayoutInflater.from(view.context)
                        .inflate(R.layout.popup_window_item_desk, null)
                    popupLayout.setOnClickListener {
                        dismiss()
                        menuItemClickListener.onClick(view)
                    }

                    contentView = popupLayout
                    width = WindowManager.LayoutParams.WRAP_CONTENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    isOutsideTouchable = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        elevation = view.resources.getDimension(R.dimen.popup_window_elevation)
                    }
                    setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            view.context,
                            R.drawable.rounded_corners_4dp
                        )
                    )

                    popupLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    val deskMargin = view.resources.getDimension(R.dimen.desk_item_spacing).toInt()
                    val xOffset = -(popupLayout.measuredWidth - it.width) + deskMargin
                    showAsDropDown(it, xOffset, 0)
                }
            }
        }
    }

    override fun unbind(holder: DeskHolder) {
        super.unbind(holder)
        holder.view.setOnClickListener(null)
    }
}

class DeskHolder : KotlinEpoxyHolder() {
    lateinit var view: View

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
    }

    val deskBarcode by bind<TextView>(R.id.deskBarcode)
    val syncedCount by bind<TextView>(R.id.syncedCount)
    val notSyncedCount by bind<TextView>(R.id.notSyncedCount)
    val notScannedCount by bind<TextView>(R.id.notScannedCount)
    val equipmentCount by bind<TextView>(R.id.equipmentCount)
    val moreButton by bind<ImageButton>(R.id.moreButton)
}