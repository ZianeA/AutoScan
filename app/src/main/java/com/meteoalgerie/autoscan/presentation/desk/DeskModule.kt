package com.meteoalgerie.autoscan.presentation.desk

import dagger.Binds
import dagger.Module

@Module
interface DeskModule {
    @Binds
    fun provideDeskView(fragment: DeskFragment): DeskView
}