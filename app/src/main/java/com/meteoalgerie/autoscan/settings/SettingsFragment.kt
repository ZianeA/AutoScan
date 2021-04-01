package com.meteoalgerie.autoscan.settings


import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding

import com.meteoalgerie.autoscan.R
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.toolbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {
    @Inject
    lateinit var presenter: SettingsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
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

        editServerButton.setOnClickListener {
            val inputDialog = InputTextDialogFragment.newInstance()
            inputDialog.title = R.string.dialog_title_edit_server
            inputDialog.inputText = server.text.toString()
            inputDialog.setInputListener(
                doOnPositiveButtonClick = { dialog, _, inputLayout, text ->
                    if (!Patterns.WEB_URL.matcher(text.toString()).matches()) {
                        inputLayout.error = getString(R.string.error_Invalid_url)
                    } else {
                        presenter.onServerEntered(text.toString())
                        dialog.dismiss()
                    }
                },
                doAfterTextChanged = { _, inputLayout, _ -> inputLayout.error = null })

            inputDialog.show(childFragmentManager, null)
        }

        editDatabaseName.setOnClickListener {
            val inputDialog = InputTextDialogFragment.newInstance()
            inputDialog.title = R.string.dialog_title_edit_database_name
            inputDialog.inputText = databaseName.text.toString()
            inputDialog.setInputListener(
                doOnPositiveButtonClick = { dialog, _, _, text ->
                    presenter.onChangeDatabaseName(text.toString())
                    dialog.dismiss()
                })

            inputDialog.show(childFragmentManager, null)
        }

        val themeModes = ThemeMode.values().map { getString(it.text) }
        editThemeButton.setOnClickListener { _ ->
            val dialog = ConfirmationDialogFragment.newInstance(
                getString(R.string.theme),
                themeModes,
                themeModes.indexOf(theme.text)
            )
            dialog.setOnCheckedChangeListener { index ->
                presenter.onChangeTheme(ThemeMode.getByIndex(index))
            }
            dialog.show(parentFragmentManager, null)
        }

        val scanModes = ScanMode.values().map { getString(it.text) }
        editScanModeButton.setOnClickListener { _ ->
            val dialog = ConfirmationDialogFragment.newInstance(
                getString(R.string.scan_mode),
                scanModes,
                scanModes.indexOf(theme.text)
            )
            dialog.setOnCheckedChangeListener { index ->
                presenter.onChangeScanMode(ScanMode.getByIndex(index))
            }
            dialog.show(parentFragmentManager, null)
        }
        editBarcodeLengthButton.setOnClickListener { _ ->
            val barcodeLength = barcodeLength.text.toString().toInt()
            val dialog = NumberPickerFragment(barcodeLength, 1, 100)
            dialog.doOnPositiveButtonClick = { presenter.onChangeBarcodeLength(it) }
            dialog.show(parentFragmentManager, null)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.serverUrl
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { server.text = it }

        presenter.databaseName
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { databaseName.text = it.toString() }

        presenter.theme
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { (name, mode) ->
                theme.text = getString(name)
                (requireActivity() as AppCompatActivity).delegate.localNightMode = mode
            }

        presenter.scanMode
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { scanMode.text = getString(it) }

        presenter.barcodeLength
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { barcodeLength.text = it.toString() }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
