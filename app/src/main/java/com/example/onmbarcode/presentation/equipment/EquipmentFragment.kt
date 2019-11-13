package com.example.onmbarcode.presentation.equipment


import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.Desk
import com.google.android.material.animation.ArgbEvaluatorCompat
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_equipment.view.*
import javax.inject.Inject
import kotlin.math.hypot

/**
 * A simple [Fragment] subclass.
 * Use the [EquipmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EquipmentFragment : Fragment(), EquipmentView {
    @Inject
    lateinit var presenter: EquipmentPresenter

    lateinit var epoxyController: EquipmentEpoxyController
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_equipment, container, false)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(rootView.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        recyclerView = rootView.equipmentRecyclerView
        recyclerView.setItemSpacingDp(EQUIPMENT_ITEM_SPACING)
        // TODO refactor, move to EpoxyModel
        epoxyController = EquipmentEpoxyController(View.OnClickListener {
            val cardView = it as CardView
            val greenColor = ContextCompat.getColor(it.context, R.color.materialGreen)
            val redColor = ContextCompat.getColor(it.context, R.color.materialRed)

            if (cardView.cardBackgroundColor.defaultColor == greenColor)
                return@OnClickListener

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //TODO refactor everything
                //get the center for the clipping circle relative to view.
                val revealView = cardView.findViewById<ImageView>(R.id.revealView)
                val cx = revealView.width / 2
                val cy = revealView.height / 2

                // get the final radius for the clipping circle
                val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

                // create the animator for this view (the start radius is zero)
                val anim =
                    ViewAnimationUtils.createCircularReveal(revealView, 0, cy, 0f, finalRadius * 2)

                // start the animation
                revealView.visibility = View.VISIBLE
                anim.start()
            } else {
                ObjectAnimator.ofObject(
                    cardView,
                    "cardBackgroundColor",
                    ArgbEvaluator(),
                    redColor,
                    greenColor
                ).start()
            }
        })

        return rootView
    }

    override fun onStart() {
        super.onStart()
        val selectedDesk = arguments?.getParcelable<Desk>(ARG_SELECTED_DESK)
            ?: throw IllegalStateException("Use the newInstance method to instantiate this fragment.")
        presenter.start(selectedDesk)
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun displayEquipments(equipments: List<Equipment>) {
        if (recyclerView.adapter == null) {
            recyclerView.setController(epoxyController)
        }

        epoxyController.equipments = equipments
    }

    companion object {
        private const val EQUIPMENT_ITEM_SPACING = 1
        private const val ARG_SELECTED_DESK = "selected_desk"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EquipmentFragment.
         */
        @JvmStatic
        fun newInstance(selectedDesk: Desk) =
            EquipmentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SELECTED_DESK, selectedDesk)
                }
            }
    }
}
