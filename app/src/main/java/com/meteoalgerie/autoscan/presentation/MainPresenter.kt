package com.meteoalgerie.autoscan.presentation

import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.presentation.di.ActivityScope
import com.meteoalgerie.autoscan.presentation.download.IsDownloadCompleteUseCase
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor(
    storage: PreferenceStorage,
    isDownloadCompleteUseCase: IsDownloadCompleteUseCase
) {
    val launchDestination = BehaviorRelay.create<LaunchDestination>()

    init {
        when {
            storage.user == null -> launchDestination.accept(LaunchDestination.LOGIN)
            isDownloadCompleteUseCase.execute() -> launchDestination.accept(LaunchDestination.DESK)
            else -> launchDestination.accept(LaunchDestination.DOWNLOAD)
        }
    }

    enum class LaunchDestination { LOGIN, DESK, DOWNLOAD }
}