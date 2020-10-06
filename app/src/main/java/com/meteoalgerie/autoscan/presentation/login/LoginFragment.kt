package com.meteoalgerie.autoscan.presentation.login


import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged

import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.presentation.settings.SettingsFragment
import com.google.android.material.snackbar.Snackbar
import com.meteoalgerie.autoscan.presentation.desk.DeskFragment
import com.meteoalgerie.autoscan.presentation.download.DownloadFragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import javax.inject.Inject
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
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

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            // Move toolbar below status bar
            rootView.appBarLayout.updatePadding(top = insets.systemWindowInsetTop)

            // Move content above navigation bar
            rootView.content.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        setupKeyboardListener(rootView, rootView.content)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            presenter.onLogin(usernameBox.text.toString(), passwordBox.text.toString())
        }

        usernameBox.doAfterTextChanged {
            presenter.onLoginDataChanged(it.toString(), passwordBox.text.toString())
        }

        passwordBox.doAfterTextChanged {
            presenter.onLoginDataChanged(usernameBox.text.toString(), it.toString())
        }

        passwordBox.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    presenter.onLogin(usernameBox.text.toString(), passwordBox.text.toString())
            }
            false
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun onStart() {
        super.onStart()

        presenter.canLogin
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { loginButton.isEnabled = it }

        presenter.passwordBoxState
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                when (it) {
                    LoginPresenter.TextBoxState.Idle -> passwordLayout.error = null
                    is LoginPresenter.TextBoxState.Error -> {
                        passwordLayout.error = getString(it.messageId)
                    }
                }
            }

        presenter.navigateDestination
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                when (it) {
                    LoginPresenter.NavigationDestination.DESK -> {
                        fragNavController.replaceFragment(DeskFragment.newInstance())
                    }
                    LoginPresenter.NavigationDestination.DOWNLOAD -> {
                        fragNavController.replaceFragment(DownloadFragment.newInstance())
                    }
                    LoginPresenter.NavigationDestination.SETTINGS -> {
                        fragNavController.pushFragment(SettingsFragment.newInstance())
                    }
                }
            }

        presenter.message
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe { Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_LONG).show() }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_login, menu)
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

    private fun setupKeyboardListener(view: View, scrollView: ScrollView) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            // if more than 100 pixels, its probably a keyboard...
            if (abs(view.rootView.height - (r.bottom - r.top)) > 100) {
                scrollView.scrollToBottomWithoutFocusChange()
            }
        }
    }

    // Kotlin extension to scrollView
    private fun ScrollView.scrollToBottomWithoutFocusChange() {
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY + height)
        smoothScrollBy(0, delta)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}
