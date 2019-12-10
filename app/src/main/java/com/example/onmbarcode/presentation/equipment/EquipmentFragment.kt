package com.example.onmbarcode.presentation.equipment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.desk.DeskUi
import com.example.onmbarcode.presentation.util.ItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_equipment.*
import kotlinx.android.synthetic.main.fragment_equipment.view.*
import kotlinx.android.synthetic.main.my_snackbar.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [EquipmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EquipmentFragment : Fragment(), EquipmentView {
    @Inject
    lateinit var presenter: EquipmentPresenter

    private lateinit var epoxyController: EquipmentEpoxyController
    private lateinit var recyclerView: EpoxyRecyclerView
    override var equipments: List<Equipment> = emptyList()
    override var equipmentToAnimate = ""
    private var shouldScrollToTop = false
    private var isUiUpdating = false

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
        recyclerView.addItemDecoration(
            ItemDecoration(
                resources.getDimension(R.dimen.equipment_item_spacing).toInt()
            )
        )
        epoxyController = EquipmentEpoxyController(presenter::onEquipmentConditionPicked)
        epoxyController.addModelBuildListener {
            if (shouldScrollToTop) {
                recyclerView.scrollToPosition(0)
                shouldScrollToTop = false
                isUiUpdating = false
            }
        }

        rootView.barcodeInput.addTextChangedListener(afterTextChanged = {
            presenter.onBarcodeChange(it.toString())
        })

        return rootView
    }

    override fun onStart() {
        super.onStart()
        val selectedDesk = arguments?.getParcelable<DeskUi>(ARG_SELECTED_DESK)
            ?: throw IllegalStateException("Use the newInstance method to instantiate this fragment.")
        presenter.start(selectedDesk)
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun displayEquipments() {
        if (recyclerView.adapter == null) {
            recyclerView.setController(epoxyController)
        }

        epoxyController.equipments = equipments
        EquipmentEpoxyModel.equipmentToAnimateBarcode = equipmentToAnimate
        epoxyController.requestModelBuild()
    }

    override fun displayEquipmentsDelayed() {
        if (!isUiUpdating) {
            epoxyController.equipments = equipments
            EquipmentEpoxyModel.equipmentToAnimateBarcode = equipmentToAnimate
            epoxyController.requestDelayedModelBuild(MODEL_BUILD_DELAY)
        }
    }

    //TODO Disable user scrolling while scrolling
    override fun scrollToTopAndDisplayEquipments() {
        shouldScrollToTop = true

        if (recyclerView.computeVerticalScrollOffset() == 0) {
            displayEquipments()
            return
        }

        recyclerView.smoothScrollToPosition(0)
        isUiUpdating = true
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    displayEquipments()
                    recyclerView.removeOnScrollListener(this)
                }
            }
        })
    }

    override fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

    override fun clearBarcodeInputArea() {
        barcodeInput.text.clear()
    }

    override fun displayEquipmentConditionChangedMessage() {
        //TODO Reuse this snackbar
        Snackbar.make(
            mainContent,
            "Equipment condition changed successfully",
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    override fun showErrorMessage() {
        snackbar.showMessage(R.string.unknown_error_message)
    }

    companion object {
        private const val ARG_SELECTED_DESK = "selected_desk"
        private const val MODEL_BUILD_DELAY = 200

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EquipmentFragment.
         */
        @JvmStatic
        fun newInstance(selectedDesk: DeskUi) =
            EquipmentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SELECTED_DESK, selectedDesk)
                }
            }
    }
}
