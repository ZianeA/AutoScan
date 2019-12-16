package com.example.onmbarcode.presentation.login


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged

import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.settings.SettingsFragment
import com.example.onmbarcode.presentation.desk.DeskFragment
import com.google.android.material.snackbar.Snackbar
import com.ncapdevi.fragnav.FragNavController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_login.view.toolbar
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(), LoginView {
    @Inject
    lateinit var presenter: LoginPresenter

    @Inject
    lateinit var fragNavController: FragNavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(rootView.toolbar)
            setHasOptionsMenu(true);
        }

        rootView.loginButton.setOnClickListener {
            presenter.onLogin(username.text.toString(), password.text.toString())
        }

        rootView.username.doAfterTextChanged {
            presenter.onLoginDataChanged(
                it.toString(),
                rootView.password.text.toString()
            )
        }

        rootView.password.apply {
            doAfterTextChanged {
                presenter.onLoginDataChanged(
                    rootView.username.text.toString(),
                    it.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        presenter.onLogin(username.text.toString(), password.text.toString())
                }
                false
            }
        }

        return rootView
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                presenter.onSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun displayDeskScreen() {
        fragNavController.replaceFragment(DeskFragment.newInstance())
    }

    override fun enableLogin() {
        loginButton.isEnabled = true
    }

    override fun disableLogin() {
        loginButton.isEnabled = false
    }

    override fun displayWrongCredentialsMessage() {
        passwordLayout.isErrorEnabled = true
        passwordLayout.error = resources.getString(R.string.invalid_credentials)
    }

    override fun displayLoginFailedMessage() {
        Snackbar.make(passwordLayout, R.string.login_failed, Snackbar.LENGTH_LONG).apply {
            setAction(
                R.string.action_retry
            ) { presenter.onLogin(username.text.toString(), password.text.toString()) }
            show()
        }
    }

    override fun hideErrorMessage() {
        passwordLayout.isErrorEnabled = false
    }

    override fun displaySettingsScreen() {
        fragNavController.pushFragment(SettingsFragment.newInstance())
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}
