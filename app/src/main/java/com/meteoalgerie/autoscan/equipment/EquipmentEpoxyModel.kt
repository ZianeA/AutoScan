package com.meteoalgerie.autoscan.equipment

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.postDelayed
import androidx.core.widget.ImageViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.elevation.ElevationOverlayProvider
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.util.KotlinEpoxyHolder
import com.google.android.material.textfield.TextInputLayout
import com.meteoalgerie.autoscan.equipment.Equipment.*
import kotlinx.android.synthetic.main.popup_window_equipment_moved.view.*
import java.util.*

@EpoxyModelClass(layout = R.layout.item_equipment)
abstract class EquipmentEpoxyModel : EpoxyModelWithHolder<EquipmentHolder>() {
    @EpoxyAttribute
    lateinit var equipment: Equipment

    @EpoxyAttribute
    lateinit var dropdownMenuItemClickListener: AdapterView.OnItemClickListener

    @ExperimentalStdlibApi
    override fun bind(holder: EquipmentHolder) {
        super.bind(holder)

        holder.apply {
            equipmentType.text = equipment.type.capitalize(Locale.FRENCH)

            equipmentBarcode.apply {
                text = equipment.barcode
                visibility =
                    if (equipment.scanState == ScanState.NotScanned) View.GONE else View.VISIBLE
            }

            dropdownMenu.setAdapter(
                ArrayAdapter(
                    view.context,
                    R.layout.dropdown_menu_popup_item,
                    equipmentConditions
                )
            )
            dropdownMenu.inputType = InputType.TYPE_NULL
            dropdownMenu.filters = emptyArray()
            dropdownMenu.setText(equipmentConditions[equipment.condition.ordinal], false)
            dropdownMenu.onItemClickListener = dropdownMenuItemClickListener
            dropdownMenu.isEnabled = when (equipment.scanState) {
                ScanState.ScannedAndSynced -> true
                ScanState.ScannedButNotSynced -> true
                else -> false
            }
            dropdownLayout.isEndIconVisible = dropdownMenu.isEnabled

            // Show progress bar and warning icon
            warningIcon.visibility = View.GONE
            progressBar.visibility = View.GONE

            val isLoading = loadingEquipment.any { it == equipment.id }

            if (isLoading) {
                val progressBarColor = ContextCompat.getColor(view.context, android.R.color.white)
                progressBar.apply {
                    indeterminateDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.MULTIPLY)
                    visibility = View.VISIBLE
                }
            } else if (equipment.deskId != equipment.previousDeskId && equipment.scanState != ScanState.NotScanned) {
                warningIcon.visibility = View.VISIBLE
            }

            // Show equipment moved tooltip on click
            warningIcon.setOnClickListener {
                it.isEnabled = false
                showTooltip(view.context, it)
            }

            // Pick scan state message and background color
            val messageResource: Int
            val equipmentColor: Int

            when {
                isLoading || equipmentToAnimateId == equipment.id -> {
                    messageResource = R.string.equipment_pending_message
                    equipmentColor = notScannedColor
                }
                equipment.scanState == ScanState.ScannedAndSynced -> {
                    messageResource = R.string.equipment_synced_message
                    equipmentColor = syncedColor
                }
                equipment.scanState == ScanState.ScannedButNotSynced -> {
                    messageResource = R.string.equipment_scanned_message
                    equipmentColor = scannedColor
                }
                else -> {
                    messageResource = R.string.equipment_not_scanned_message
                    equipmentColor = notScannedColor
                }
            }

            // Set scan state message
            val message = view.context.getString(messageResource)
            scanStateMessage.text = message

            // Set cardview background color
            revealView.visibility = View.INVISIBLE
            cardView.setCardBackgroundColor(equipmentColor)

            if (equipmentToAnimateId == equipment.id) {
                animateEquipmentColor(this)
                equipmentToAnimateId = null
            }
        }
    }

    private fun showTooltip(context: Context, anchor: View) {
        val tooltip = PopupWindow(context).apply {
            val popupLayout = LayoutInflater.from(context)
                .inflate(R.layout.popup_window_equipment_moved, null)

            // Set pointer position
            val pointer = popupLayout.pointer
            (pointer.layoutParams as ConstraintLayout.LayoutParams).marginEnd += anchor.width / 2

            // Add elevation overlay in dark theme
            val elevationOverlayColor =
                ElevationOverlayProvider(context).compositeOverlayWithThemeSurfaceColorIfNeeded(
                    pointer.elevation
                )
            ViewCompat.setBackgroundTintList(
                pointer,
                ColorStateList.valueOf(elevationOverlayColor)
            )
            ViewCompat.setBackgroundTintList(
                popupLayout.messageBackground,
                ColorStateList.valueOf(elevationOverlayColor)
            )

            contentView = popupLayout
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            isOutsideTouchable = true

            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            popupLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val deskMargin =
                context.resources.getDimension(R.dimen.equipment_item_spacing).toInt()
            val elevation =
                context.resources.getDimension(R.dimen.popup_window_elevation).toInt()
            val xOffset = -(popupLayout.measuredWidth - anchor.width) + deskMargin + elevation
            showAsDropDown(anchor, xOffset, 0)

            Handler().postDelayed(TOOLTIP_DURATION) { dismiss() }

            setOnDismissListener {
                equipmentMoved.remove(equipment.id)
                anchor.postDelayed(50) { anchor.isEnabled = true }
                tooltipList.remove(this)
            }
        }

        tooltipList.add(tooltip)
    }

    override fun onViewDetachedFromWindow(holder: EquipmentHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.view.clearAnimation()
    }

    override fun unbind(holder: EquipmentHolder) {
        super.unbind(holder)
        holder.apply {
            revealView.scaleX = 1f
            revealView.visibility = View.INVISIBLE
            progressBar.visibility = View.GONE
            warningIcon.visibility = View.GONE
        }
    }

    private fun animateEquipmentColor(holder: EquipmentHolder) {
        val endColor: Int
        val endMessage: String

        when (equipment.scanState) {
            ScanState.ScannedAndSynced -> {
                endColor = holder.syncedColor
                endMessage = holder.view.context.getString(R.string.equipment_synced_message)
            }
            ScanState.ScannedButNotSynced -> {
                endColor = holder.scannedColor
                endMessage = holder.view.context.getString(R.string.equipment_scanned_message)
            }
            else -> throw IllegalStateException("Invalid equipment state")
        }

        holder.apply {
            revealView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            ImageViewCompat.setImageTintList(revealView, ColorStateList.valueOf(endColor))
            revealView.animate()
                .scaleXBy(cardView.width.toFloat())
                .setDuration(ANIMATION_DURATION)
                .withEndAction {
                    revealView.visibility = View.INVISIBLE
                    revealView.scaleX = 1f
                    cardView.setCardBackgroundColor(endColor)
                    scanStateMessage.text = endMessage

                    // Show equipment moved tooltip
                    if (equipmentMoved.find { it == equipment.id } != null) {
                        equipmentMoved.remove(equipment.id)
                        warningIcon.performClick()
                    }
                }
                .start()
        }
    }

    companion object {
        private const val ANIMATION_DURATION: Long = 1000
        private const val TOOLTIP_DURATION: Long = 4000
        var equipmentToAnimateId: Int? = null
        var loadingEquipment: List<Int> = emptyList()
        var equipmentMoved: MutableList<Int> = mutableListOf()
        var tooltipList: MutableList<PopupWindow> = mutableListOf()
    }
}

class EquipmentHolder : KotlinEpoxyHolder() {
    lateinit var view: View
    var syncedColor: Int = 0
    var notScannedColor: Int = 0
    var scannedColor: Int = 0
    lateinit var equipmentConditions: Array<String>

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
        syncedColor = ContextCompat.getColor(view.context, R.color.scanned_and_synced)
        notScannedColor = ContextCompat.getColor(view.context, R.color.not_scanned)
        scannedColor = ContextCompat.getColor(view.context, R.color.scanned_but_not_synced)
        equipmentConditions = view.resources.getStringArray(R.array.equipment_condition)
    }

    val equipmentBarcode by bind<TextView>(R.id.equipmentBarcode)
    val equipmentType by bind<TextView>(R.id.equipmentType)
    val cardView by bind<CardView>(R.id.equipmentCardView)
    val revealView by bind<ImageView>(R.id.revealView)
    val progressBar by bind<ProgressBar>(R.id.progressBar)
    val dropdownMenu by bind<AutoCompleteTextView>(R.id.dropdownMenu)
    val dropdownLayout by bind<TextInputLayout>(R.id.textInputLayout)
    val scanStateMessage by bind<TextView>(R.id.scanStateMessage)
    val warningIcon by bind<ImageView>(R.id.warningIcon)
}