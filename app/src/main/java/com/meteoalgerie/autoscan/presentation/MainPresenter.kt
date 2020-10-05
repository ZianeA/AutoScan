package com.meteoalgerie.autoscan.presentation

import com.meteoalgerie.autoscan.data.user.UserDao
import com.meteoalgerie.autoscan.presentation.di.ActivityScope
import com.meteoalgerie.autoscan.presentation.util.applySchedulers
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor(userDao: UserDao, schedulerProvider: SchedulerProvider) {

    val launchDestination = userDao.get()
        .applySchedulers(schedulerProvider)
        // If user is logged in go to desk screen
        .map { LaunchDestination.DESK }
        // Else go to login screen
        .onErrorReturnItem(LaunchDestination.LOGIN)

    enum class LaunchDestination { LOGIN, DESK }
}