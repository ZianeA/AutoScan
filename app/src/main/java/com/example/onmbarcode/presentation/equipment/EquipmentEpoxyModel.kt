package com.example.onmbarcode.presentation.equipment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.OnModelClickListener
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import kotlin.math.hypot

@EpoxyModelClass(layout = R.layout.item_equipment)
abstract class EquipmentEpoxyModel : EpoxyModelWithHolder<EquipmentHolder>() {
    @EpoxyAttribute
    lateinit var equipment: Equipment

    //TODO rename
    @EpoxyAttribute
    lateinit var clickListener: ((equipment: Equipment) -> Unit)

    override fun bind(holder: EquipmentHolder) {
        super.bind(holder)
        holder.apply {
            equipmentBarcode.text =
                view.context.getString(R.string.equipment_barcode, equipment.barcode)
            equipmentType.text = view.context.getString(R.string.equipment_type, equipment.type)

            val equipmentColor = if (equipment.isScanned) greenColor else redColor
            cardView.setBackgroundColor(equipmentColor)
            cardView.setOnClickListener {
                if (equipment.isScanned) return@setOnClickListener

                animateEquipmentColor(
                    cardView,
                    revealView,
                    greenColor,
                    redColor
                )
            }
        }
    }

    override fun unbind(holder: EquipmentHolder) {
        super.unbind(holder)
        holder.revealView.visibility = View.INVISIBLE
    }

    private fun animateEquipmentColor(
        cardView: CardView,
        revealView: View,
        scannedColor: Int,
        unscannedColor: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //get the center for the clipping circle relative to view.
            val cx = revealView.width / 2
            val cy = revealView.height / 2

            // get the final radius for the clipping circle
            val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat() * 2

            // create the animator for this view (the start radius is zero)
            val anim =
                ViewAnimationUtils.createCircularReveal(revealView, 0, cy, 0f, finalRadius)

            // start the animation
            revealView.visibility = View.VISIBLE
            anim.addListener(onEnd = {
                clickListener.invoke(equipment)
                cardView.setCardBackgroundColor(scannedColor)
                revealView.visibility = View.INVISIBLE
            })
            anim.start()
        } else {
            val anim = ObjectAnimator.ofObject(
                cardView,
                "cardBackgroundColor",
                ArgbEvaluator(),
                unscannedColor,
                scannedColor
            )
            anim.addListener(onEnd = { clickListener.invoke(equipment) })
            anim.start()
        }
    }
}

class EquipmentHolder : KotlinEpoxyHolder() {
    lateinit var view: View
    var greenColor: Int = 0
    var redColor: Int = 0

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
        greenColor = ContextCompat.getColor(view.context, R.color.materialGreen)
        redColor = ContextCompat.getColor(view.context, R.color.materialRed)
    }

    val equipmentBarcode by bind<TextView>(R.id.equipmentBarcode)
    val equipmentType by bind<TextView>(R.id.equipmentType)
    val cardView by bind<CardView>(R.id.equipmentCardView)
    val revealView by bind<ImageView>(R.id.revealView)
}