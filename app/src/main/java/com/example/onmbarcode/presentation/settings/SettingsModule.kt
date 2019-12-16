package com.example.onmbarcode.presentation.settings

import com.example.onmbarcode.data.KeyValueStore
import com.example.onmbarcode.data.PreferencesKeyValueStore
import dagger.Binds
import dagger.Module

@Module
interface SettingsModule {
    @Binds
    fun provideSettingsView(settingsFragment: SettingsFragment): SettingsView
}