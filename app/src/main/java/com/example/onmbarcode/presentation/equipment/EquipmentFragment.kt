package com.example.onmbarcode.presentation.equipment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.util.ItemDecoration
import com.example.onmbarcode.presentation.util.MySnackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_equipment.*
import kotlinx.android.synthetic.main.fragment_equipment.snackbar
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
    private var scrollToTop = false
    private var isUiUpdating = false

    override var isScrolling = false
        set(value) {
            if (value) scrollDisabler.visibility = View.VISIBLE
            else scrollDisabler.visibility = View.GONE

            field = value
        }

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

        rootView.scrollUpButton.apply {
            hide()
            setOnClickListener { smoothScrollToTop() }
        }

        rootView.equipmentRecyclerView.apply {
            recyclerView = this
            addItemDecoration(
                ItemDecoration(
                    resources.getDimension(R.dimen.equipment_item_spacing).toInt()
                )
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0) {
                        val topItem =
                            (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                        if (topItem > MAX_MANUAL_SCROLL) rootView.scrollUpButton.show()
                        else rootView.scrollUpButton.hide()

                    } else rootView.scrollUpButton.hide()
                }
            })
        }


        rootView.scrollDisabler.setOnClickListener { }
        epoxyController = EquipmentEpoxyController(presenter::onEquipmentConditionPicked)
        epoxyController.addModelBuildListener {
            if (scrollToTop) {
                recyclerView.scrollToPosition(0)
                scrollToTop = false
                isUiUpdating = false
            }
        }

        rootView.barcodeInput.apply {
            addTextChangedListener(afterTextChanged = {
                presenter.onBarcodeChange(it.toString(), selectedDesk.id)
            })

            requestFocus()
            getSystemService(context, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(windowToken, 0)
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
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

    override fun displayEquipments(equipment: List<Equipment>) {
        if (recyclerView.adapter == null) {
            recyclerView.setController(epoxyController)
        }

        epoxyController.desk = selectedDesk
        epoxyController.equipments = equipment
        epoxyController.requestModelBuild()
    }

    // Smooth scroll to top -> display equipment with the new order
    // After displaying equipment, the first element will be hidden so we scroll to the top again

    // While smoothing scroll to top, equipment list could change. So, we store the newest equipment list
    // and we display it at the end of scrolling
    override fun scrollToTop() {
        isScrolling = true

        if (recyclerView.computeVerticalScrollOffset() == 0) {
            isScrolling = false
            scrollToTop = true
            return
        }

        smoothScrollToTop()
        isUiUpdating = true
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    isScrolling = false
                    presenter.onScrollEnded(selectedDesk.id)
                    scrollToTop = true
                    recyclerView.removeOnScrollListener(this)
                }
            }
        })
    }

    private fun smoothScrollToTop() {
        val targetItem = 0
        val topItem =
            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val distance = topItem - targetItem
        val anchorItem = when {
            distance > MAX_SMOOTH_SCROLL -> targetItem + MAX_SMOOTH_SCROLL
            distance < -MAX_SMOOTH_SCROLL -> targetItem - MAX_SMOOTH_SCROLL
            else -> topItem
        }

        if (anchorItem != topItem) recyclerView.scrollToPosition(anchorItem)
        recyclerView.smoothScrollToPosition(targetItem)
    }

    override fun animateEquipment(equipmentId: Int) {
        EquipmentEpoxyModel.equipmentToAnimateId = equipmentId
        if (!isUiUpdating) {
            epoxyController.requestDelayedModelBuild(MODEL_BUILD_DELAY)
        }
    }

    override fun displayProgressBarForEquipment(equipmentId: Int) {
        EquipmentEpoxyModel.loadingEquipments.add(equipmentId)
    }

    override fun hideProgressBarForEquipment(equipmentId: Int) {
        EquipmentEpoxyModel.loadingEquipments.remove(equipmentId)
    }

    override fun clearBarcodeInputArea() {
        barcodeInput.text.clear()
    }

    override fun displayEquipmentConditionChangedMessage() {
        snackbar.showMessage(
            getString(R.string.equipment_condition_changed_message),
            MySnackbar.LENGTH_SHORT
        )
    }

    override fun showErrorMessage() {
        snackbar.showMessage(R.string.unknown_error_message)
    }

    override fun showUnknownBarcodeMessage() {
        snackbar.showMessage(R.string.unknown_barcode_message, MySnackbar.LENGTH_SHORT)
    }

    override fun showEquipmentAlreadyScannedMessage() {
        snackbar.showMessage(
            getString(R.string.equipment_already_scanned_message),
            MySnackbar.LENGTH_SHORT
        )
    }

    override fun showEquipmentMovedMessage() {
        snackbar.showMessage(R.string.equipment_moved_message, MySnackbar.LENGTH_SHORT)
    }

    private val selectedDesk
        get() = arguments?.getParcelable<Desk>(ARG_SELECTED_DESK)
            ?: throw IllegalStateException("Use the newInstance method to instantiate this fragment.")

    companion object {
        private const val ARG_SELECTED_DESK = "selected_desk"
        private const val MODEL_BUILD_DELAY = 200
        private const val MAX_SMOOTH_SCROLL = 15
        private const val MAX_MANUAL_SCROLL = 30

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
