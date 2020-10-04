package com.meteoalgerie.autoscan.presentation.settings


import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding

import com.meteoalgerie.autoscan.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.toolbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment(), SettingsView {
    @Inject
    lateinit var presenter: SettingsPresenter
    lateinit var inputDialog: InputTextDialogFragment

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

        rootView.editServerButton.setOnClickListener {
            inputDialog = InputTextDialogFragment.newInstance()
            inputDialog.setInputText(rootView.server.text.toString())
            inputDialog.setInputListener(doOnPositiveButtonClick = { dialog, input, inputLayout, text ->
                if (Patterns.WEB_URL.matcher(text.toString()).matches().not()) {
                    inputLayout.error = getString(R.string.error_Invalid_url)
                } else {
                    presenter.onServerEntered(text.toString())
                    dialog.dismiss()
                }
            }, doAfterTextChanged = { input, inputLayout, text ->
                inputLayout.error = null
            })

            inputDialog.show(childFragmentManager, inputDialogTag)
        }

        rootView.editThemeButton.setOnClickListener {
            ThemeDialogFragment(theme.text.toString()) { presenter.onChangeTheme(it) }
                .show(parentFragmentManager, null)
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun changeTheme(@StringRes name: Int, @NightMode mode: Int) {
        theme.text = getString(name)
        (requireActivity() as AppCompatActivity).delegate.localNightMode = mode
    }

    override fun displayServerUrl(serverUrl: String) {
        server.text = serverUrl
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    companion object {
        fun newInstance() = SettingsFragment()
        const val inputDialogTag = "EditServerDialog"
    }
}
