package com.meteoalgerie.autoscan.equipment

import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class EquipmentModule {
    @Provides
    fun provideEquipmentView(fragment: EquipmentFragment): EquipmentView = fragment

    @Provides
    fun provideEquipmentDesk(fragment: EquipmentFragment) = fragment.selectedDesk
}