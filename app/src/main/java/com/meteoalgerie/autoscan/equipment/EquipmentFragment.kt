package com.meteoalgerie.autoscan.equipment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxrelay2.PublishRelay

import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.desk.Desk
import com.meteoalgerie.autoscan.common.util.ItemDecoration
import com.meteoalgerie.autoscan.common.util.hide
import com.meteoalgerie.autoscan.common.util.show
import com.meteoalgerie.autoscan.common.util.showIf
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import dagger.android.support.AndroidSupportInjection
import hu.akarnokd.rxjava2.operators.ObservableTransformers
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_equipment.*
import kotlinx.android.synthetic.main.fragment_equipment.view.*
import kotlinx.android.synthetic.main.fragment_equipment.view.appBarLayout
import kotlinx.android.synthetic.main.fragment_equipment.view.toolbar
import timber.log.Timber
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
    private var scrollToTop = false
    private val scrollingValve = PublishRelay.create<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_equipment, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            // Move toolbar below status bar
            rootView.appBarLayout.updatePadding(top = insets.systemWindowInsetTop)

            // Move content above navigation bar
            rootView.content.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        (activity as AppCompatActivity).apply {
            setSupportActionBar(rootView.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollUpButton.apply {
            hide()
            setOnClickListener { smoothScrollToTop() }
        }

        scrollDisabler.setOnClickListener { }
        epoxyController =
            EquipmentEpoxyController(presenter::onEquipmentConditionPicked, presenter::onTagClicked)
        epoxyController.addModelBuildListener {
            if (scrollToTop) {
                equipmentRecyclerView.scrollToPosition(0)
                scrollToTop = false
            }
        }

        equipmentRecyclerView.apply {
            setController(epoxyController)

            addItemDecoration(
                ItemDecoration(resources.getDimension(R.dimen.equipment_item_spacing).toInt())
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

        barcodeBox.apply {
            addTextChangedListener(afterTextChanged = {
                presenter.onBarcodeChange(it.toString(), selectedDesk.id)
                EquipmentEpoxyModel.tooltipList.forEach { tooltip -> tooltip.dismiss() }
            })

            requestFocus()
            getSystemService(context, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(windowToken, 0)
        }

        barcodeSubmitButton.setOnClickListener {
            presenter.onSubmitBarcode(barcodeBox.text.toString(), selectedDesk.id)
        }

        swipeRefreshLayout.setOnRefreshListener { presenter.onRefresh(selectedDesk.id) }
    }

    override fun onStart() {
        super.onStart()

        presenter.desk
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- desk = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                epoxyController.desk = it
                epoxyController.requestModelBuild()
            }

        presenter.selectedTags
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- selectedTags = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                epoxyController.selectedTags = it
                epoxyController.requestModelBuild()
            }

        presenter.equipment
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- equipment = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                epoxyController.equipment = it
                epoxyController.requestModelBuild()
            }

        presenter.isManualScan
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- manualScan = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { barcodeSubmitButton.showIf { _ -> it } }

        presenter.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- isLoading = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                if (it) {
                    epoxyController.skeletonEquipmentCount = selectedDesk.equipmentCount
                } else {
                    epoxyController.skeletonEquipmentCount = 0
                }

                epoxyController.requestModelBuild()
            }

        presenter.isRefreshing
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { Timber.d("---- isRefreshing = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { swipeRefreshLayout.isRefreshing = it }

        presenter.scanningEquipment
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- scanningEquipment = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                EquipmentEpoxyModel.loadingEquipment = it
                epoxyController.requestModelBuild()
            }

        // events
        presenter.message
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { Timber.d("---- message = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_LONG).apply {
                    setAnchorView(R.id.inputLayout)
                    show()
                }
            }

        presenter.displayEquipmentMoved
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- displayEquipmentMoved = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                EquipmentEpoxyModel.equipmentMoved.add(it)
                epoxyController.requestModelBuild()
            }

        presenter.animateEquipment
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- animateEquipment = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                EquipmentEpoxyModel.equipmentToAnimate = it
                epoxyController.requestModelBuild()
            }

        presenter.clearBarcodeBox
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { Timber.d("---- clearBarcodeBox = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { barcodeBox.text.clear() }

        presenter.scrollToTop
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ObservableTransformers.valve(scrollingValve, true))
            .doOnNext { Timber.d("---- scrollToTop = $it") }
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { scrollToTop() }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    // Smooth scroll to top -> display equipment with the new order
    // After displaying equipment, the first element will be hidden so we scroll to the top again
    private fun scrollToTop() {
        scrollDisabler.show()
        scrollingValve.accept(false)

        if (equipmentRecyclerView.computeVerticalScrollOffset() == 0) {
            scrollDisabler.hide()
            scrollingValve.accept(true)
            scrollToTop = true
            return
        }

        smoothScrollToTop()
        equipmentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    scrollDisabler.hide()
                    scrollingValve.accept(true)
                    scrollToTop = true
                    recyclerView.removeOnScrollListener(this)
                }
            }
        })
    }

    private fun smoothScrollToTop() {
        val targetItem = 0
        val topItem =
            (equipmentRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val distance = topItem - targetItem
        val anchorItem = when {
            distance > MAX_SMOOTH_SCROLL -> targetItem + MAX_SMOOTH_SCROLL
            distance < -MAX_SMOOTH_SCROLL -> targetItem - MAX_SMOOTH_SCROLL
            else -> topItem
        }

        if (anchorItem != topItem) equipmentRecyclerView.scrollToPosition(anchorItem)
        equipmentRecyclerView.smoothScrollToPosition(targetItem)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    val selectedDesk
        get() = arguments?.getParcelable<Desk>(ARG_SELECTED_DESK)
            ?: throw IllegalStateException("Use the newInstance method to instantiate this fragment.")

    companion object {
        private const val ARG_SELECTED_DESK = "selected_desk"
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
