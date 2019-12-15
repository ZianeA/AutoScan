package com.example.onmbarcode.presentation

import com.example.onmbarcode.presentation.desk.DeskFragment
import com.example.onmbarcode.presentation.desk.DeskModule
import com.example.onmbarcode.presentation.di.ActivityScope
import com.example.onmbarcode.presentation.di.FragmentScope
import com.example.onmbarcode.presentation.equipment.EquipmentFragment
import com.example.onmbarcode.presentation.equipment.EquipmentModule
import com.example.onmbarcode.presentation.login.LoginFragment
import com.example.onmbarcode.presentation.login.LoginModule
import com.example.onmbarcode.presentation.region.RegionFragment
import com.example.onmbarcode.presentation.region.RegionModule
import com.example.onmbarcode.presentation.station.StationFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
class MainModule {
    @ActivityScope
    @Provides
    fun provideFragNavController(mainActivity: MainActivity) = mainActivity.fragNavController

    @ActivityScope
    @Provides
    fun provideMainView(mainActivity: MainActivity): MainView = mainActivity

    @Module
    interface FragmentBindingModule {
        @FragmentScope
        @ContributesAndroidInjector(modules = [RegionModule::class])
        fun regionFragment(): RegionFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [DeskModule::class])
        fun deskFragment(): DeskFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [LoginModule::class])
        fun loginFragment(): LoginFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [EquipmentModule::class])
        fun equipmentFragment(): EquipmentFragment

        @FragmentScope
        @ContributesAndroidInjector
        fun stationFragment(): StationFragment
    }
}