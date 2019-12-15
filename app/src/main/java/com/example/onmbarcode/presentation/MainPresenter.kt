package com.example.onmbarcode.presentation

import com.example.onmbarcode.data.OdooService
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
    private val odooService: OdooService,
    private val schedulerProvider: SchedulerProvider
) {
    private val disposables = CompositeDisposable()

    fun start() {
        val disposable = userRepository.getUser()
            .flatMap { odooService.authenticate(it.username, it.password) }
            .applySchedulers(schedulerProvider)
            .subscribe(
                { view.displayDeskScreen() },
                { view.displayDeskScreen() },
                { view.displayLoginScreen() })

        disposables.add(disposable)
    }

    fun stop() {
        disposables.clear()
    }
}