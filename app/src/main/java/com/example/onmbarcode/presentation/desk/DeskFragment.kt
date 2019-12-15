package com.example.onmbarcode.presentation.desk


import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.equipment.EquipmentFragment
import com.example.onmbarcode.presentation.util.ItemDecoration
import com.ncapdevi.fragnav.FragNavController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_desk.*
import kotlinx.android.synthetic.main.fragment_desk.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [DeskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeskFragment : Fragment(), DeskView {
    @Inject
    lateinit var presenter: DeskPresenter

    @Inject
    lateinit var fragNavController: FragNavController

    //TODO move to presenter
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
        }

        recyclerView = rootView.deskRecyclerView
        recyclerView.addItemDecoration(
            ItemDecoration(
                resources.getDimension(R.dimen.desk_item_spacing).toInt()
            )
        )

        rootView.barcodeSubmitButton.setOnClickListener {
            presenter.onBarcodeEntered(rootView.barcodeInput.text.toString())
        }


        rootView.barcodeInput.apply {
            (activity as AppCompatActivity).window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            requestFocus()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun displayDesks(desks: List<DeskUi>) {
        if (recyclerView.adapter == null) {
            recyclerView.setController(epoxyController)
        }
        epoxyController.desks = desks
    }

    override fun displayEquipmentsScreen(desk: DeskUi) {
        fragNavController.pushFragment(EquipmentFragment.newInstance(desk))
    }

    override fun showUnknownBarcodeMessage() {
        snackbar.showMessage(R.string.unknown_barcode_message)
    }

    override fun showErrorMessage() {
        snackbar.showMessage(R.string.unknown_error_message)
    }

    override fun clearBarcodeInputArea() {
        barcodeInput.text.clear()
    }

    override fun disableBarcodeInput() {
        barcodeSubmitButton.isEnabled = false
    }

    override fun enableBarcodeInput() {
        barcodeSubmitButton.isEnabled = true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DeskFragment.
         */
        @JvmStatic
        fun newInstance() = DeskFragment()
    }
}
