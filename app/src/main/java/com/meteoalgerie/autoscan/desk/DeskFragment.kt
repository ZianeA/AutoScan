package com.meteoalgerie.autoscan.desk


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.google.android.material.snackbar.Snackbar

import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.equipment.EquipmentFragment
import com.meteoalgerie.autoscan.login.LoginFragment
import com.meteoalgerie.autoscan.common.util.showIf
import com.ncapdevi.fragnav.FragNavController
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_desk.*
import kotlinx.android.synthetic.main.fragment_desk.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [DeskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeskFragment : Fragment() {
    @Inject
    lateinit var presenter: DeskPresenter

    @Inject
    lateinit var fragNavController: FragNavController

    private lateinit var epoxyController: DeskEpoxyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onStart()
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

        (activity as AppCompatActivity).setSupportActionBar(rootView.toolbar)

        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        epoxyController =
            DeskEpoxyController(presenter::onDeskClicked) { d, _ -> presenter.onHideDeskClicked(d) }
        deskRecyclerView.setItemSpacingDp(8)

        barcodeSubmitButton.setOnClickListener {
            presenter.onBarcodeEntered(barcodeBox.text.toString())
        }

        barcodeBox.apply {
            (activity as AppCompatActivity).window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            requestFocus()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.desks
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                if (deskRecyclerView.adapter == null) {
                    deskRecyclerView.setController(epoxyController)
                }
                epoxyController.desks = it
            }

        presenter.displayEmptyState
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { display -> emptyStateView.showIf { display } }

        presenter.canScan
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { barcodeSubmitButton.isEnabled = it }

        presenter.clearBarcodeBox
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { barcodeBox.text.clear() }

        presenter.message
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).apply {
                    setAnchorView(R.id.barcodeLayout)
                    show()
                }
            }

        presenter.navigationDestination
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                when (it) {
                    DeskPresenter.NavigationDestination.Login -> {
                        fragNavController.replaceFragment(LoginFragment.newInstance())
                    }
                    is DeskPresenter.NavigationDestination.Equipment -> {
                        fragNavController.pushFragment(EquipmentFragment.newInstance(it.desk))
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
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
