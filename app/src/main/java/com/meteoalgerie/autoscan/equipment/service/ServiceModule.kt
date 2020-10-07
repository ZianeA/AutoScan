package com.meteoalgerie.autoscan.equipment.service

import dagger.Binds
import dagger.Module

@Module
interface ServiceModule {
    @Binds
    fun provideSyncBackgroundService(syncWorkManager: SyncWorkManager): SyncBackgroundService
}