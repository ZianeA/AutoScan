package com.example.onmbarcode.presentation.di

import com.example.onmbarcode.presentation.MainActivity
import com.example.onmbarcode.presentation.MainModule
import com.example.onmbarcode.presentation.region.RegionModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class, MainModule.FragmentBindingModule::class])
    abstract fun mainActivity(): MainActivity
}
