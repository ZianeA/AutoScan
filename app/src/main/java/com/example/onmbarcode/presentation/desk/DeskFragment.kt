package com.example.onmbarcode.presentation.desk


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.equipment.EquipmentFragment
import com.example.onmbarcode.presentation.region.Region
import com.example.onmbarcode.presentation.region.RegionFragment
import com.example.onmbarcode.presentation.station.Station
import com.ncapdevi.fragnav.FragNavController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_desk.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [DeskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeskFragment : Fragment(), DeskView {
    @Inject
    lateinit var deskPresenter: DeskPresenter

    @Inject
    lateinit var fragNavController: FragNavController

    private val epoxyController =
        DeskEpoxyController { fragNavController.pushFragment(EquipmentFragment.newInstance(it)) }
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_desk, container, false)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(rootView.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        recyclerView = rootView.deskRecyclerView
        recyclerView.setItemSpacingDp(DESK_ITEM_SPACING)

        return rootView
    }

    override fun onStart() {
        super.onStart()
        deskPresenter.start()
    }

    override fun onStop() {
        super.onStop()
        deskPresenter.stop()
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun displayDesks(desks: List<Desk>) {
        if (recyclerView.adapter == null) {
            recyclerView.setController(epoxyController)
        }
        epoxyController.desks = desks
    }

    companion object {
        private const val DESK_ITEM_SPACING = 8
        private const val ARG_SELECTED_STATION = "selected_station"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DeskFragment.
         */
        @JvmStatic
        fun newInstance(selectedStation: Station) =
            DeskFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SELECTED_STATION, selectedStation)
                }
            }
    }
}
