package com.example.onmbarcode.presentation.desk

import dagger.Binds
import dagger.Module

@Module
interface DeskModule {
    @Binds
    fun provideDeskView(fragment: DeskFragment): DeskView
}