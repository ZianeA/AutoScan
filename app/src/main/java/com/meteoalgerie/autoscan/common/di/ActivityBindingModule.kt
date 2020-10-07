package com.meteoalgerie.autoscan.common.di

import com.meteoalgerie.autoscan.common.main.MainActivity
import com.meteoalgerie.autoscan.common.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class, MainModule.FragmentBindingModule::class])
    abstract fun mainActivity(): MainActivity
}
