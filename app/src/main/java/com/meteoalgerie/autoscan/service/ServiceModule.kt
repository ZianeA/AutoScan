package com.meteoalgerie.autoscan.service

import dagger.Binds
import dagger.Module

@Module
interface ServiceModule {
    @Binds
    fun provideSyncBackgroundService(syncWorkManager: SyncWorkManager): SyncBackgroundService
}