package com.example.onmbarcode.presentation.equipment

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_equipment)
abstract class EquipmentEpoxyModel : EpoxyModelWithHolder<EquipmentHolder>() {
    @EpoxyAttribute
    lateinit var equipment: Equipment

    override fun bind(holder: EquipmentHolder) {
        super.bind(holder)
        holder.apply {
            equipmentBarcode.text =
                view.context.getString(R.string.equipment_barcode, equipment.barcode)
            equipmentType.text = view.context.getString(R.string.equipment_type, equipment.type)
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
}