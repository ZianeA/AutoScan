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
import com.meteoalgerie.autoscan.settings.SettingsFragment
import com.ncapdevi.fragnav.FragNavController
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_desk.*
import kotlinx.android.synthetic.main.fragment_desk.view.*
import androidx.appcompat.view.ActionMode
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
    private var actionMode: ActionMode? = null

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

        compatActivity.setSupportActionBar(rootView.toolbar)

        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        epoxyController = DeskEpoxyController()
        epoxyController.onDeskClickListener = presenter::onDeskClicked
        epoxyController.onDeskLongClickListener = {
            if (actionMode == null) {
                actionMode = compatActivity.startSupportActionMode(actionModeCallback)
            }
            val selectionCount = epoxyController.selectedDesks.size
            actionMode?.title = selectionCount.toString()
        }
        deskRecyclerView.setItemSpacingDp(8)

        barcodeSubmitButton.setOnClickListener {
            presenter.onBarcodeEntered(barcodeBox.text.toString())
        }

        barcodeBox.apply {
            compatActivity.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            requestFocus()
        }
    }

    private val compatActivity: AppCompatActivity
        get() = activity as AppCompatActivity

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
                actionMode?.finish()

                when (it) {
                    DeskPresenter.NavigationDestination.Login -> {
                        fragNavController.replaceFragment(LoginFragment.newInstance())
                    }
                    DeskPresenter.NavigationDestination.Settings -> {
                        fragNavController.pushFragment(SettingsFragment.newInstance())
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
            R.id.action_settings -> {
                presenter.onSettingsClicked()
                true
            }
            R.id.action_logout -> {
                presenter.onLogoutClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        // Called when the action mode is created; startActionMode() was called
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            mode.menuInflater.inflate(R.menu.context_menu_desk, menu)
            return true
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_hide -> {
                    presenter.onHideDesksClicked(epoxyController.selectedDesks.toList())
                    mode.finish() // Action picked, so close the CAB
                    true
                }
                else -> false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            epoxyController.selectedDesks.clear()
            epoxyController.requestModelBuild()
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
