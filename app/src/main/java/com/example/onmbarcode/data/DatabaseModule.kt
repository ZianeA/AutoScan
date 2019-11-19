package com.example.onmbarcode.data

import android.app.Application
import androidx.room.Room
import com.example.onmbarcode.presentation.equipment.EquipmentDao
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
}