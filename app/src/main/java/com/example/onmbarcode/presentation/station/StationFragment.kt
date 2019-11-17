package com.example.onmbarcode.presentation.station


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.DeskFragment
import com.example.onmbarcode.presentation.region.Region
import com.ncapdevi.fragnav.FragNavController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_desk.view.*
import kotlinx.android.synthetic.main.fragment_station.view.*
import kotlinx.android.synthetic.main.fragment_station.view.toolbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [StationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationFragment : Fragment() {
    @Inject
    lateinit var fragNavController: FragNavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_station, container, false)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(rootView.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        rootView.stationRecyclerView.apply {
            setItemSpacingDp(STATION_ITEM_SPACING)
            val stationEpoxyController = StationEpoxyController {
                fragNavController.pushFragment(DeskFragment.newInstance(it))
            }
            setControllerAndBuildModels(stationEpoxyController)
        }

        return rootView
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val STATION_ITEM_SPACING = 8
        private const val ARG_SELECTED_REGION = "selected_region"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StationFragment.
         */
        @JvmStatic
        fun newInstance(selectedRegion: Region) =
            StationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SELECTED_REGION, selectedRegion)
                }
            }
    }
}
