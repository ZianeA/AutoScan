package com.meteoalgerie.autoscan.presentation

import com.meteoalgerie.autoscan.data.user.UserDao
import com.meteoalgerie.autoscan.presentation.di.ActivityScope
import com.meteoalgerie.autoscan.presentation.util.applySchedulers
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor(
    private val view: MainView,
    private val userDao: UserDao,
    private val schedulerProvider: SchedulerProvider
) {
    private val disposables = CompositeDisposable()

    fun start(refresh: Boolean) {
        if (refresh.not()) return

        val disposable = userDao.get()
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