package com.meteoalgerie.autoscan.presentation.di

import com.meteoalgerie.autoscan.presentation.MainActivity
import com.meteoalgerie.autoscan.presentation.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class, MainModule.FragmentBindingModule::class])
    abstract fun mainActivity(): MainActivity
}
