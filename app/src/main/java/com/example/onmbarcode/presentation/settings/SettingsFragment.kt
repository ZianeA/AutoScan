package com.example.onmbarcode.presentation.settings


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.example.onmbarcode.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.dialog_input_text.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
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

        rootView.editServerButton.setOnClickListener {
            inputDialog = InputTextDialogFragment.newInstance()
            inputDialog.setInputText(rootView.server.text.toString())
            inputDialog.setInputListener(doOnPositiveButtonClick = { dialog, input, inputLayout, text ->
                if (Patterns.WEB_URL.matcher(text.toString()).matches().not()) {
                    inputLayout.isErrorEnabled = true
                    inputLayout.error = getString(R.string.error_Invalid_url)
                } else {
                    presenter.onServerEntered(text.toString())
                    dialog.dismiss()
                }
            }, doAfterTextChanged = { input, inputLayout, text ->
                inputLayout.isErrorEnabled = false
            })

            inputDialog.show(childFragmentManager, inputDialogTag)
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun displayServerUrl(serverUrl: String) {
        server.text = serverUrl
    }

    companion object {
        fun newInstance() = SettingsFragment()
        const val inputDialogTag = "EditServerDialog"
    }
}
