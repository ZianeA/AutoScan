package com.example.onmbarcode.presentation

import com.example.onmbarcode.presentation.desk.DeskFragment
import com.example.onmbarcode.presentation.desk.DeskModule
import com.example.onmbarcode.presentation.di.ActivityScope
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.region.RegionFragment
import com.example.onmbarcode.presentation.region.RegionModule
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
        @ContributesAndroidInjector(modules = [RegionModule::class])
        fun regionFragment(): RegionFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [DeskModule::class])
        fun deskFragment(): DeskFragment
    }
}