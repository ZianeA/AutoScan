package com.meteoalgerie.autoscan.common.main

import com.meteoalgerie.autoscan.settings.SettingsFragment
import com.meteoalgerie.autoscan.desk.DeskFragment
import com.meteoalgerie.autoscan.common.di.ActivityScope
import com.meteoalgerie.autoscan.common.di.FragmentScope
import com.meteoalgerie.autoscan.download.DownloadFragment
import com.meteoalgerie.autoscan.equipment.EquipmentFragment
import com.meteoalgerie.autoscan.equipment.EquipmentModule
import com.meteoalgerie.autoscan.login.LoginFragment
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