package com.example.onmbarcode.presentation.di

import com.example.onmbarcode.data.KeyValueStore
import com.example.onmbarcode.data.PreferencesKeyValueStore
import com.example.onmbarcode.presentation.util.scheduler.IoSchedulerProvider
import com.example.onmbarcode.presentation.util.scheduler.SchedulerProvider
import dagger.Binds
import dagger.Module

@Module
interface AppModule {
    @Binds
    fun provideIoSchedulerProvider(scheduler: IoSchedulerProvider): SchedulerProvider
}