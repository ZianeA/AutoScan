package com.meteoalgerie.autoscan.common.database

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideRedditDatabase(app: Application): OnmBarcodeDatabase {
        return Room.databaseBuilder(app, OnmBarcodeDatabase::class.java, "onm_barcode")
            .build()
    }

    @Provides
    fun provideEquipmentDao(database: OnmBarcodeDatabase) = database.equipmentDao()

    @Provides
    fun provideDeskDao(database: OnmBarcodeDatabase) = database.deskDao()
}