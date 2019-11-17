package com.example.onmbarcode.presentation.equipment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.Desk
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_equipment.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [EquipmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EquipmentFragment : Fragment(), EquipmentView,
    EquipmentStateDialogFragment.EquipmentStateDialogListener {
    @Inject
    lateinit var presenter: EquipmentPresenter

    private lateinit var epoxyController: EquipmentEpoxyController
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
        epoxyController = EquipmentEpoxyController(presenter::onEquipmentScanned)

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

    override fun scrollToTop() {
        val lastVisibleItemPosition =
            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (lastVisibleItemPosition < PAGE_SIZE) {
            recyclerView.scrollToPosition(0)
        }
    }

    override fun displayEquipmentStatePicker(currentState: Equipment.EquipmentState) {
        EquipmentStateDialogFragment.newInstance(currentState.ordinal)
            .show(childFragmentManager, "EquipmentStateDialogFragment")
    }

    override fun onEquipmentStatePicked(index: Int) {
        presenter.onEquipmentStatePicked(index)
    }

    companion object {
        private const val EQUIPMENT_ITEM_SPACING = 1
        private const val ARG_SELECTED_DESK = "selected_desk"
        private const val PAGE_SIZE = 6 //TODO update this

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
