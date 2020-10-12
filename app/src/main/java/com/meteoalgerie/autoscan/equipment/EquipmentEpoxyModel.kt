package com.meteoalgerie.autoscan.equipment

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.postDelayed
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.elevation.ElevationOverlayProvider
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.util.KotlinEpoxyHolder
import com.google.android.material.textfield.TextInputLayout
import com.meteoalgerie.autoscan.common.util.hide
import com.meteoalgerie.autoscan.common.util.show
import com.meteoalgerie.autoscan.common.util.showIf
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
                showIf { equipment.scanState != ScanState.NotScanned }
            }

            dropdownMenu.apply {
                setAdapter(
                    ArrayAdapter(
                        context,
                        R.layout.dropdown_menu_popup_item,
                        equipmentConditions
                    )
                )

                inputType = InputType.TYPE_NULL
                filters = emptyArray()
                setText(equipmentConditions[equipment.condition.ordinal], false)
                onItemClickListener = dropdownMenuItemClickListener
                isEnabled = equipment.scanState != ScanState.NotScanned
            }
            dropdownLayout.isEndIconVisible = dropdownMenu.isEnabled

            val isLoading = loadingEquipment.any { it == equipment.id }

            progressBar.showIf { isLoading }
            warningIcon.showIf {
                !isLoading && equipment.deskId != equipment.previousDeskId
                        && equipment.scanState != ScanState.NotScanned
            }

            // Show equipment moved tooltip on click
            warningIcon.setOnClickListener {
                it.isEnabled = false
                showTooltip(context, it)
            }

            // Pick scan state message and background color
            val messageResource: Int
            val equipmentColor: Int

            when {
                isLoading || equipmentToAnimate?.first == equipment.id -> {
                    messageResource = R.string.equipment_pending_message
                    equipmentColor = notScannedColor
                }
                equipment.scanState == ScanState.ScannedAndSynced -> {
                    messageResource = R.string.equipment_synced_message
                    equipmentColor = syncedColor
                }
                equipment.scanState == ScanState.ScannedButNotSynced -> {
                    messageResource = R.string.equipment_scanned_message
                    equipmentColor = unsyncedColor
                }
                else -> {
                    messageResource = R.string.equipment_not_scanned_message
                    equipmentColor = notScannedColor
                }
            }

            // Set scan state message
            scanStateMessage.text = context.getString(messageResource)

            // Set cardview background color
            revealView.hide()
            cardView.setCardBackgroundColor(equipmentColor)

            if (equipmentToAnimate?.first == equipment.id) {
                when (equipmentToAnimate?.second) {
                    EquipmentPresenter.AnimationType.SUCCESS -> animateToGreen(this)
                    EquipmentPresenter.AnimationType.FAILURE -> animateToOrange(this)
                }
                equipmentToAnimate = null
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

    override fun unbind(holder: EquipmentHolder) {
        super.unbind(holder)
        holder.revealView.visibility = View.INVISIBLE
    }

    private fun animateToGreen(holder: EquipmentHolder) {
        holder.apply {
            revealView.animate()
                .scaleXBy(cardView.width.toFloat())
                .setDuration(800L)
                .withStartAction {
                    revealView.setBackgroundColor(syncedColor)
                    revealView.show()
                    progressBar.hide()
                }
                .withEndAction {
                    revealView.hide()
                    revealView.scaleX = 1f
                    cardView.setCardBackgroundColor(syncedColor)
                    scanStateMessage.text = context.getString(R.string.equipment_synced_message)

                    // Show equipment moved tooltip
                    if (equipmentMoved.any { it == equipment.id }) {
                        equipmentMoved.remove(equipment.id)
                        warningIcon.performClick()
                    }
                }
                .setStartDelay(400L)
                .start()
        }
    }

    private fun animateToOrange(holder: EquipmentHolder) {
        holder.apply {
            ObjectAnimator.ofArgb(
                cardView,
                "cardBackgroundColor",
                notScannedColor,
                unsyncedColor
            ).apply {
                duration = 800L
                startDelay = 400L
                doOnEnd {
                    scanStateMessage.text =
                        context.getString(R.string.equipment_scanned_message)

                    // Show equipment moved tooltip
                    if (equipmentMoved.any { it == equipment.id }) {
                        equipmentMoved.remove(equipment.id)
                        warningIcon.performClick()
                    }
                }
                start()
            }
        }
    }

    companion object {
        private const val TOOLTIP_DURATION = 4000L
        var equipmentToAnimate: Pair<Int, EquipmentPresenter.AnimationType>? = null
        var loadingEquipment = emptyList<Int>()
        var equipmentMoved = mutableListOf<Int>()
        var tooltipList = mutableListOf<PopupWindow>()
    }
}

class EquipmentHolder : KotlinEpoxyHolder() {
    lateinit var view: View
    lateinit var context: Context
    var syncedColor: Int = 0
    var notScannedColor: Int = 0
    var unsyncedColor: Int = 0
    lateinit var equipmentConditions: Array<String>

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
        context = itemView.context
        syncedColor = ContextCompat.getColor(context, R.color.scanned_and_synced)
        notScannedColor = ContextCompat.getColor(context, R.color.not_scanned)
        unsyncedColor = ContextCompat.getColor(context, R.color.scanned_but_not_synced)
        equipmentConditions = view.resources.getStringArray(R.array.equipment_condition)
    }

    val equipmentBarcode by bind<TextView>(R.id.equipmentBarcode)
    val equipmentType by bind<TextView>(R.id.equipmentType)
    val cardView by bind<CardView>(R.id.equipmentCardView)
    val revealView by bind<View>(R.id.revealView)
    val progressBar by bind<ProgressBar>(R.id.progressBar)
    val dropdownMenu by bind<AutoCompleteTextView>(R.id.dropdownMenu)
    val dropdownLayout by bind<TextInputLayout>(R.id.textInputLayout)
    val scanStateMessage by bind<TextView>(R.id.scanStateMessage)
    val warningIcon by bind<ImageView>(R.id.warningIcon)
}