package com.example.onmbarcode.presentation.equipment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import com.google.android.material.textfield.TextInputLayout
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
            val isLoading = loadingEquipments.find { it == equipment.id } != null
            if (isLoading) {
                val progressBarColor = ContextCompat.getColor(view.context, android.R.color.white)
                progressBar.apply {
                    indeterminateDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.MULTIPLY)
                    visibility = View.VISIBLE
                }
            } else progressBar.visibility = View.GONE

            if (!isLoading && equipment.deskId != equipment.previousDeskId) {
                warningIcon.visibility = View.VISIBLE
            } else warningIcon.visibility = View.GONE

            // Pick scan state message and background color
            val messageResource: Int
            val equipmentColor: Int

            when {
                equipment.scanState == ScanState.ScannedAndSynced -> {
                    messageResource = R.string.equipment_synced_message
                    equipmentColor = syncedColor
                }
                isLoading && equipment.scanState == ScanState.ScannedButNotSynced -> {
                    messageResource = R.string.equipment_pending_message
                    equipmentColor = notScannedColor
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
            if (equipmentToAnimateId == equipment.id) {
                animateEquipmentColor(this, equipmentColor, message)
                equipmentToAnimateId = null

            } else {
                cardView.setCardBackgroundColor(equipmentColor)
            }
        }
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

    private fun animateEquipmentColor(holder: EquipmentHolder, endColor: Int, endMessage: String) {
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
                }
                .start()
        }
    }

    companion object {
        private const val ANIMATION_DURATION: Long = 1000
        var equipmentToAnimateId: Int? = null
        var loadingEquipments: MutableList<Int> = mutableListOf()
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