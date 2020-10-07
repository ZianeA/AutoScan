package com.meteoalgerie.autoscan.login

import androidx.annotation.StringRes
import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.common.di.FragmentScope
import com.meteoalgerie.autoscan.download.IsDownloadCompleteUseCase
import com.meteoalgerie.autoscan.common.util.applySchedulers
import com.meteoalgerie.autoscan.common.scheduler.SchedulerProvider
import hu.akarnokd.rxjava2.subjects.UnicastWorkSubject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@FragmentScope
class LoginPresenter @Inject constructor(
    private val authApi: AuthApi,
    private val schedulerProvider: SchedulerProvider,
    private val storage: PreferenceStorage,
    private val isDownloadCompleteUseCase: IsDownloadCompleteUseCase
) {
    private val disposables = CompositeDisposable()

    val canLogin = BehaviorRelay.createDefault(false)
    val passwordBoxState = BehaviorRelay.create<TextBoxState>()
    val message: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
    val navigationDestination: UnicastWorkSubject<NavigationDestination> =
        UnicastWorkSubject.create()

    fun onLogin(username: String, password: String) {
        canLogin.accept(false)
        disposables += authApi.authenticate(username, password)
            .applySchedulers(schedulerProvider)
            .subscribeBy(
                onSuccess = { userId ->
                    if (isDownloadCompleteUseCase.execute()) {
                        navigationDestination.onNext(NavigationDestination.DESK)
                    } else {
                        navigationDestination.onNext(NavigationDestination.DOWNLOAD)
                    }
                    storage.user =
                        User(
                            userId,
                            password
                        )
                },
                onError = { error ->
                    canLogin.accept(true)

                    if (error is IllegalArgumentException) {
                        passwordBoxState.accept(TextBoxState.Error(R.string.invalid_credentials))
                    } else {
                        message.onNext(R.string.login_failed)
                    }
                })
    }

    fun onLoginDataChanged(username: String?, password: String?) {
        passwordBoxState.accept(TextBoxState.Idle)

        if (!username.isNullOrEmpty() && username.isNotBlank()
            && !password.isNullOrEmpty() && password.isNotBlank()
        ) {
            canLogin.accept(true)
        } else {
            canLogin.accept(false)
        }
    }

    fun onSettings() = navigationDestination.onNext(NavigationDestination.SETTINGS)

    fun onCleared() {
        disposables.clear()
    }

    sealed class TextBoxState {
        object Idle : TextBoxState()
        data class Error(@StringRes val messageId: Int) : TextBoxState()
    }

    enum class NavigationDestination { DESK, DOWNLOAD, SETTINGS }
}