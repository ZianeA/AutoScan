package com.meteoalgerie.autoscan.common.di

import com.meteoalgerie.autoscan.common.scheduler.IoSchedulerProvider
import com.meteoalgerie.autoscan.common.scheduler.SchedulerProvider
import dagger.Binds
import dagger.Module

@Module
interface AppModule {
    @Binds
    fun provideIoSchedulerProvider(scheduler: IoSchedulerProvider): SchedulerProvider
}