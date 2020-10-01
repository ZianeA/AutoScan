package com.meteoalgerie.autoscan.presentation.desk


import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.airbnb.epoxy.EpoxyRecyclerView

import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.presentation.equipment.EquipmentFragment
import com.meteoalgerie.autoscan.presentation.login.LoginFragment
import com.meteoalgerie.autoscan.presentation.util.ItemDecoration
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

    private lateinit var recyclerView: EpoxyRecyclerView

    private lateinit var epoxyController: DeskEpoxyController

    private var delayScanDeskMessage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController =
            DeskEpoxyController(presenter::onDeskClicked) { d, _ -> presenter.onHideDeskClicked(d) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_desk, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            // Move toolbar below status bar
            rootView.appBarLayout.updatePadding(top = insets.systemWindowInsetTop)

            // Move content above navigation bar
            rootView.content.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        (activity as AppCompatActivity).apply {
            setSupportActionBar(rootView.toolbar)
        }

        recyclerView = rootView.deskRecyclerView
        recyclerView.setItemSpacingDp(8)

        rootView.barcodeSubmitButton.setOnClickListener {
            presenter.onBarcodeEntered(rootView.barcodeInput.text.toString())
        }


        rootView.barcodeInput.apply {
            (activity as AppCompatActivity).window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            requestFocus()
        }

        setHasOptionsMenu(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_desk, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                presenter.onLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun displayDesks(desks: List<Desk>) {
        if (recyclerView.adapter == null) {
            recyclerView.setController(epoxyController)
        }
        epoxyController.desks = desks
    }

    override fun displayEquipmentsScreen(desk: Desk) {
        fragNavController.pushFragment(EquipmentFragment.newInstance(desk))
    }

    override fun displayUnknownBarcodeMessage() {
        snackbar.showMessage(R.string.unknown_barcode_message)
    }

    override fun displayGenericErrorMessage() {
        snackbar.showMessage(R.string.unknown_error_message)
    }

    override fun clearBarcodeInputArea() {
        barcodeInput.text.clear()
    }

    override fun disableBarcodeInput() {
        barcodeSubmitButton.apply {
            isEnabled = false
            ViewCompat.setBackgroundTintList(
                this,
                ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.darker_gray))
            )
        }
    }

    override fun enableBarcodeInput() {
        barcodeSubmitButton.apply {
            isEnabled = true
            ViewCompat.setBackgroundTintList(
                this,
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
            )
        }
    }

    override fun displayLoginScreen() {
        fragNavController.replaceFragment(LoginFragment.newInstance())
    }

    override fun displayScanDeskMessage() {
        if (delayScanDeskMessage) return

        scanDeskMessage.visibility = View.VISIBLE
        barcodeIcon.visibility = View.VISIBLE
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
