package com.example.onmbarcode.presentation.equipment

import dagger.Binds
import dagger.Module

@Module
interface EquipmentModule {
    @Binds
    fun provideEquipmentView(fragment: EquipmentFragment): EquipmentView
}