package com.example.onmbarcode.presentation.desk

import com.example.onmbarcode.data.Mapper
import dagger.Binds
import dagger.Module

@Module
interface DeskModule {
    @Binds
    fun provideDeskView(fragment: DeskFragment): DeskView

    @Binds
    fun provideDeskUiMapper(mapper: DeskUiMapper): Mapper<DeskUi, Desk>
}