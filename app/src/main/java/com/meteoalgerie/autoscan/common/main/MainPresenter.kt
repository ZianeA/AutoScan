package com.meteoalgerie.autoscan.common.main

import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.common.di.ActivityScope
import com.meteoalgerie.autoscan.download.IsDownloadCompleteUseCase
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