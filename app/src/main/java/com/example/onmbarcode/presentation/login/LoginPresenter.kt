package com.example.onmbarcode.presentation.login

import com.example.onmbarcode.data.OdooService
import com.example.onmbarcode.data.user.UserDao
import com.example.onmbarcode.data.user.UserRepository
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class LoginPresenter @Inject constructor(
    private val view: LoginView,
    private val odooService: OdooService,
    private val schedulerProvider: SchedulerProvider,
    private val userRepository: UserRepository
) {
    private val disposables = CompositeDisposable()

    fun onLogin(username: String, password: String) {
        val disposable = odooService.authenticate(username, password)
            .flatMap {
                userRepository.addUser(User(it, username, password))
                    .andThen(Maybe.just(it))
            }
            .applySchedulers(schedulerProvider)
            .subscribe(
                { view.displayDeskScreen() },
                { view.displayLoginFailedMessage() },
                { view.displayWrongCredentialsMessage() })

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
    }
}