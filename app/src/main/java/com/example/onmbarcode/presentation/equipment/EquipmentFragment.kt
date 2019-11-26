package com.example.onmbarcode.presentation.equipment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.Desk
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_equipment.*
import kotlinx.android.synthetic.main.fragment_equipment.view.*
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
    override var equipmentToAnimate = -1
    private var shouldScrollToTop = false
    private var isScrolling = false

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
            EquipmentItemDecoration(
                resources.getDimension(R.dimen.equipment_item_spacing).toInt()
            )
        )
        epoxyController = EquipmentEpoxyController(presenter::onEquipmentConditionPicked)
        epoxyController.addModelBuildListener {
            if (shouldScrollToTop) {
                recyclerView.scrollToPosition(0)
                shouldScrollToTop = false
            }
        }

        rootView.barcodeInput.addTextChangedListener(afterTextChanged = {
            presenter.onBarcodeChange(it.toString())
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
    }

    override fun displayEquipmentsDelayed() {
        if (!isScrolling) displayEquipments()
    }

    //Disable user scrolling while scrolling
    override fun scrollToTopAndDisplayEquipments() {
        shouldScrollToTop = true

        if (recyclerView.computeVerticalScrollOffset() == 0) {
            displayEquipments()
            return
        }

        recyclerView.smoothScrollToPosition(0)
        isScrolling = true
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    isScrolling = false
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
        Toast.makeText(context, "Equipment condition changed successfully", Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
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
