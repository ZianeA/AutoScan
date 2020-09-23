package com.meteoalgerie.autoscan.presentation.equipment

import dagger.Binds
import dagger.Module

@Module
interface EquipmentModule {
    @Binds
    fun provideEquipmentView(fragment: EquipmentFragment): EquipmentView
}