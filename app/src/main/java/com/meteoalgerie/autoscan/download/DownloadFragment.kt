package com.meteoalgerie.autoscan.download

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.desk.DeskFragment
import com.ncapdevi.fragnav.FragNavController
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DownloadFragment : Fragment() {
    @Inject
    lateinit var fragNavController: FragNavController

    @Inject
    lateinit var downloadService: DownloadBackgroundService

    @Inject
    lateinit var isDownloadCompleteUseCase: IsDownloadCompleteUseCase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isDownloadCompleteUseCase.execute()) {
            fragNavController.replaceFragment(DeskFragment.newInstance())
        } else {
            downloadService.downloadData()
        }

        WorkManager.getInstance(requireActivity().applicationContext)
            .getWorkInfosForUniqueWorkLiveData(DownloadBackgroundService.WORK_NAME_DOWNLOAD)
            .observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty() && it.first().state == WorkInfo.State.SUCCEEDED) {
                    fragNavController.replaceFragment(DeskFragment.newInstance())
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DownloadFragment.
         */
        @JvmStatic
        fun newInstance() = DownloadFragment()
    }
}