package com.example.onmbarcode.service

import dagger.Binds
import dagger.Module

@Module
interface ServiceModule {
    @Binds
    fun provideSyncBackgroundService(syncWorkManager: SyncWorkManager): SyncBackgroundService
}