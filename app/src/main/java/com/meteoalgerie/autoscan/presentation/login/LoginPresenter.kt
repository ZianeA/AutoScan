package com.meteoalgerie.autoscan.presentation.login

import androidx.annotation.StringRes
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.data.OdooService
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.data.user.User
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import com.meteoalgerie.autoscan.presentation.download.IsDownloadCompleteUseCase
import com.meteoalgerie.autoscan.presentation.util.applySchedulers
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import hu.akarnokd.rxjava2.subjects.UnicastWorkSubject
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@FragmentScope
class LoginPresenter @Inject constructor(
    private val odooService: OdooService,
    private val schedulerProvider: SchedulerProvider,
    private val storage: PreferenceStorage,
    private val isDownloadCompleteUseCase: IsDownloadCompleteUseCase
) {
    private val disposables = CompositeDisposable()

    val canLogin = BehaviorRelay.createDefault(false)
    val passwordBoxState = BehaviorRelay.create<TextBoxState>()
    val message: UnicastWorkSubject<Int> = UnicastWorkSubject.create()
    val navigateDestination: UnicastWorkSubject<NavigationDestination> = UnicastWorkSubject.create()

    fun onLogin(username: String, password: String) {
        canLogin.accept(false)
        disposables += odooService.authenticate(username, password)
            .applySchedulers(schedulerProvider)
            .subscribeBy(
                onSuccess = { userId ->
                    if (isDownloadCompleteUseCase.execute()) {
                        navigateDestination.onNext(NavigationDestination.DESK)
                    } else {
                        navigateDestination.onNext(NavigationDestination.DOWNLOAD)
                    }
                    storage.user = User(userId, password)
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

    fun onSettings() = navigateDestination.onNext(NavigationDestination.SETTINGS)

    fun onCleared() {
        disposables.clear()
    }

    sealed class TextBoxState {
        object Idle : TextBoxState()
        data class Error(@StringRes val messageId: Int) : TextBoxState()
    }

    enum class NavigationDestination { DESK, DOWNLOAD, SETTINGS }
}