package com.meteoalgerie.autoscan.presentation

import com.meteoalgerie.autoscan.presentation.settings.SettingsFragment
import com.meteoalgerie.autoscan.presentation.desk.DeskFragment
import com.meteoalgerie.autoscan.presentation.di.ActivityScope
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import com.meteoalgerie.autoscan.presentation.download.DownloadFragment
import com.meteoalgerie.autoscan.presentation.equipment.EquipmentFragment
import com.meteoalgerie.autoscan.presentation.equipment.EquipmentModule
import com.meteoalgerie.autoscan.presentation.login.LoginFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
class MainModule {
    @ActivityScope
    @Provides
    fun provideFragNavController(mainActivity: MainActivity) = mainActivity.fragNavController

    @Module
    interface FragmentBindingModule {
        @FragmentScope
        @ContributesAndroidInjector
        fun deskFragment(): DeskFragment

        @FragmentScope
        @ContributesAndroidInjector
        fun loginFragment(): LoginFragment

        @FragmentScope
        @ContributesAndroidInjector
        fun settingsFragment(): SettingsFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [EquipmentModule::class])
        fun equipmentFragment(): EquipmentFragment

        @FragmentScope
        @ContributesAndroidInjector
        fun downloadFragment(): DownloadFragment
    }
}