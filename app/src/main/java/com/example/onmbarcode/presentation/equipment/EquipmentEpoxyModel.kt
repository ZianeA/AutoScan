package com.example.onmbarcode.presentation.equipment

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.OnModelClickListener
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_equipment)
abstract class EquipmentEpoxyModel : EpoxyModelWithHolder<EquipmentHolder>() {
    @EpoxyAttribute
    lateinit var equipment: Equipment

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: EquipmentHolder) {
        super.bind(holder)
        holder.apply {
            equipmentBarcode.text =
                view.context.getString(R.string.equipment_barcode, equipment.barcode)
            equipmentType.text = view.context.getString(R.string.equipment_type, equipment.type)

            val equipmentColor =
                if (equipment.isScanned) ContextCompat.getColor(view.context, R.color.materialGreen)
                else ContextCompat.getColor(view.context, R.color.materialRed)

            cardView.setCardBackgroundColor(equipmentColor)
            cardView.setOnClickListener(clickListener) //TODO refactor
        }
    }
}

class EquipmentHolder : KotlinEpoxyHolder() {
    lateinit var view: View

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
    }

    val equipmentBarcode by bind<TextView>(R.id.equipmentBarcode)
    val equipmentType by bind<TextView>(R.id.equipmentType)
    val cardView by bind<CardView>(R.id.equipmentCardView)
}