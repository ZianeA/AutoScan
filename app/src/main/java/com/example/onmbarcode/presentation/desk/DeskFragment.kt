package com.example.onmbarcode.presentation.desk


import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.equipment.EquipmentFragment
import com.example.onmbarcode.presentation.login.LoginFragment
import com.example.onmbarcode.presentation.util.ItemDecoration
import com.google.android.material.animation.AnimatorSetCompat
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

    private val epoxyController =
        DeskEpoxyController { presenter.onDeskClicked(it) }

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

    override fun displayDownloadViews() {
        downloadProgressBar.isIndeterminate = true
        downloadProgressBar.visibility = View.VISIBLE
        downloadMessage.visibility = View.VISIBLE
    }

    override fun setDownloadProgress(percentage: Int) {
        downloadProgressBar.isIndeterminate = false
        downloadProgressBar.progress = percentage
    }

    override fun hideDownloadViews() {
        val downloadMessageAnimator = downloadMessage.animate()
            .alpha(0f)
            .withEndAction { downloadMessage.visibility = View.GONE }

        downloadProgressBar.animate()
            .alpha(0f)
            .withStartAction {
                animateDownloadCompleteMessage(downloadMessageAnimator.duration)
            }
            .withEndAction {
                downloadProgressBar.visibility = View.GONE
                downloadMessageAnimator.start()
            }
            .start()
    }

    private fun animateDownloadCompleteMessage(delay: Long) {
        downloadCompleteMessage.apply {
            val currentPosY = translationY
            translationY = currentPosY + 100
            animate()
                .setStartDelay(delay)
                .withStartAction { visibility = View.VISIBLE }
                .alpha(1f)
                .translationY(currentPosY)
                .withEndAction {
                    animate()
                        .setStartDelay(1500)
                        .alpha(0f)
                        .withEndAction { visibility = View.GONE }
                        .start()
                }
                .start()
            alpha = 0f
        }
    }

    override fun displayLoginScreen() {
        fragNavController.replaceFragment(LoginFragment.newInstance())
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
