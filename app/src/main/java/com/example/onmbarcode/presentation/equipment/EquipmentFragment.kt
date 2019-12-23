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
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.DeskUi
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

        recyclerView = rootView.equipmentRecyclerView
        recyclerView.addItemDecoration(
            ItemDecoration(
                resources.getDimension(R.dimen.equipment_item_spacing).toInt()
            )
        )
        rootView.scrollDisabler.setOnClickListener {  }
        epoxyController = EquipmentEpoxyController(presenter::onEquipmentConditionPicked)
        epoxyController.addModelBuildListener {
            if (scrollToTop) {
                recyclerView.scrollToPosition(0)
                scrollToTop = false
                isUiUpdating = false
            }
        }

        rootView.barcodeInput.addTextChangedListener(afterTextChanged = {
            presenter.onBarcodeChange(it.toString(), selectedDesk.id)
        })

        rootView.barcodeInput.apply {
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

        epoxyController.equipments = equipment
        epoxyController.requestModelBuild()
    }

    // Smooth scroll to top -> display equipment with the new order
    // After displaying equipment, the first element will be hidden so we scroll to the top again

    // While smoothing scroll to top, equipment list could change. So, we store the newest equipment list
    // and we display it at the end of scrolling
    //TODO Disable user scrolling while scrolling
    override fun scrollToTop() {
        isScrolling = true

        if (recyclerView.computeVerticalScrollOffset() == 0) {
            isScrolling = false
            scrollToTop = true
            return
        }

        recyclerView.smoothScrollToPosition(0)
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
        get() = arguments?.getParcelable<DeskUi>(ARG_SELECTED_DESK)
            ?: throw IllegalStateException("Use the newInstance method to instantiate this fragment.")

    companion object {
        private const val ARG_SELECTED_DESK = "selected_desk"
        private const val MODEL_BUILD_DELAY = 200

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EquipmentFragment.
         */
        @JvmStatic
        fun newInstance(selectedDesk: DeskUi) =
            EquipmentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SELECTED_DESK, selectedDesk)
                }
            }
    }
}
