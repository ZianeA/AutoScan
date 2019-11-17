package com.example.onmbarcode.presentation.region


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.DeskFragment
import com.example.onmbarcode.presentation.station.StationFragment
import com.ncapdevi.fragnav.FragNavController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_region.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [RegionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegionFragment : Fragment() {
    @Inject
    lateinit var fragNavController: FragNavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_region, container, false)

        rootView.regionRecyclerView.apply {
            setItemSpacingDp(REGION_ITEM_SPACING)
            val regionEpoxyController = RegionEpoxyController {
                fragNavController.pushFragment(StationFragment.newInstance(it))
            }
            setControllerAndBuildModels(regionEpoxyController)
        }

        return rootView
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val REGION_ITEM_SPACING = 8

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RegionFragment.
         */
        @JvmStatic
        fun newInstance() = RegionFragment()
    }
}
