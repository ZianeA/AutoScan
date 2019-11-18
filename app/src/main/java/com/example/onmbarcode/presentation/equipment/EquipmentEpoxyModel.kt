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
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder
import kotlin.math.hypot

@EpoxyModelClass(layout = R.layout.item_equipment)
abstract class EquipmentEpoxyModel : EpoxyModelWithHolder<EquipmentHolder>() {
    @EpoxyAttribute
    lateinit var equipment: Equipment

    lateinit var holder: EquipmentHolder

    override fun bind(holder: EquipmentHolder) {
        super.bind(holder)
        this.holder = holder
        holder.apply {
            equipmentBarcode.text =
                view.context.getString(R.string.equipment_barcode, equipment.barcode)
            equipmentType.text = view.context.getString(R.string.equipment_type, equipment.type)

            val equipmentLocalizedState =
                view.resources.getStringArray(R.array.equipment_state)[equipment.state.ordinal]
            equipmentState.text =
                view.context.getString(R.string.equipment_state, equipmentLocalizedState)

            val equipmentColor = if (equipment.isScanned) scannedColor else notScannedColor
            cardView.setBackgroundColor(equipmentColor)
        }
    }

    override fun unbind(holder: EquipmentHolder) {
        super.unbind(holder)
        holder.revealView.visibility = View.INVISIBLE
    }

    fun animateEquipmentColor(animationEndListener: (() -> Unit)) {
        holder.apply {
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
                    animationEndListener.invoke()
                    cardView.setBackgroundColor(scannedColor)
                    revealView.visibility = View.INVISIBLE
                })
                anim.duration = ANIMATION_DURATION
                anim.start()
            } else {
                val anim = ObjectAnimator.ofObject(
                    cardView,
                    "cardBackgroundColor",
                    ArgbEvaluator(),
                    notScannedColor,
                    scannedColor
                )
                anim.addListener(onEnd = { animationEndListener.invoke() })
                anim.duration = ANIMATION_DURATION
                anim.start()
            }
        }
    }

    companion object {
        private const val ANIMATION_DURATION: Long = 1000
    }
}

class EquipmentHolder : KotlinEpoxyHolder() {
    lateinit var view: View
    var scannedColor: Int = 0
    var notScannedColor: Int = 0

    override fun bindView(itemView: View) {
        super.bindView(itemView)
        view = itemView
        scannedColor = ContextCompat.getColor(view.context, R.color.materialGreen)
        notScannedColor = ContextCompat.getColor(view.context, R.color.materialRed)
    }

    val equipmentBarcode by bind<TextView>(R.id.equipmentBarcode)
    val equipmentType by bind<TextView>(R.id.equipmentType)
    val equipmentState by bind<TextView>(R.id.equipmentState)
    val cardView by bind<CardView>(R.id.equipmentCardView)
    val revealView by bind<ImageView>(R.id.revealView)
}