package com.meteoalgerie.autoscan.presentation.login

import com.meteoalgerie.autoscan.data.OdooService
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.data.user.User
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import com.meteoalgerie.autoscan.presentation.util.applySchedulers
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class LoginPresenter @Inject constructor(
    private val view: LoginView,
    private val odooService: OdooService,
    private val schedulerProvider: SchedulerProvider,
    private val storage: PreferenceStorage
) {
    private val disposables = CompositeDisposable()
    private var isLoginInProgress = false

    fun onLogin(username: String, password: String) {
        if (isLoginInProgress) return

        isLoginInProgress = true
        view.disableLogin()
        val disposable = odooService.authenticate(username, password)
            .applySchedulers(schedulerProvider)
            .subscribe(
                { userId ->
                    view.displayDeskScreen()
                    storage.user = User(userId, password)
                },
                { error ->
                    view.enableLogin()
                    isLoginInProgress = false

                    if (error is IllegalArgumentException) {
                        view.displayWrongCredentialsMessage()
                    } else {
                        view.displayLoginFailedMessage()
                    }
                })

        disposables.add(disposable)
    }

    fun onLoginDataChanged(username: String?, password: String?) {
        view.hideErrorMessage()

        if (!username.isNullOrEmpty() && username.isNotBlank()
            && !password.isNullOrEmpty() && password.isNotBlank()
        ) {
            view.enableLogin()
        } else {
            view.disableLogin()
        }
    }

    fun stop() {
        disposables.clear()
        isLoginInProgress = false
    }

    fun onSettings() {
        view.displaySettingsScreen()
    }
}