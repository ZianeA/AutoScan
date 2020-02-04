package com.example.onmbarcode.presentation

import com.example.onmbarcode.data.user.UserRepository
import com.example.onmbarcode.presentation.di.ActivityScope
import com.example.onmbarcode.presentation.util.applySchedulers
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor(
    private val view: MainView,
    private val userRepository: UserRepository,
    private val schedulerProvider: SchedulerProvider
) {
    private val disposables = CompositeDisposable()

    fun start(refresh: Boolean) {
        if (refresh.not()) return

        val disposable = userRepository.getUser()
            .applySchedulers(schedulerProvider)
            .subscribe(
                { view.displayDeskScreen() },
                { view.displayLoginScreen() })

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }
}