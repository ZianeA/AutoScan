package com.example.onmbarcode.presentation.settings

import dagger.Binds
import dagger.Module

@Module
interface SettingsModule {
    @Binds
    fun provideSettingsView(settingsFragment: SettingsFragment): SettingsView
}