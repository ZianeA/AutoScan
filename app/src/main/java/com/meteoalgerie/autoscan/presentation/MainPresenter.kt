package com.meteoalgerie.autoscan.presentation

import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.presentation.di.ActivityScope
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor(storage: PreferenceStorage) {

    val launchDestination = BehaviorRelay.create<LaunchDestination>()

    init {
        if (storage.user == null) {
            launchDestination.accept(LaunchDestination.LOGIN)
        } else {
            launchDestination.accept(LaunchDestination.DESK)
        }
    }

    enum class LaunchDestination { LOGIN, DESK }
}