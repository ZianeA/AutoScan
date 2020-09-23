package com.meteoalgerie.autoscan.presentation.di

import com.meteoalgerie.autoscan.presentation.util.scheduler.IoSchedulerProvider
import com.meteoalgerie.autoscan.presentation.util.scheduler.SchedulerProvider
import dagger.Binds
import dagger.Module

@Module
interface AppModule {
    @Binds
    fun provideIoSchedulerProvider(scheduler: IoSchedulerProvider): SchedulerProvider
}