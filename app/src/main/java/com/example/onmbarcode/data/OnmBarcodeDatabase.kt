package com.example.onmbarcode.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.equipment.EquipmentDao

@Database(entities = [Equipment::class], version = 1)
@TypeConverters(Equipment.EquipmentStateConverter::class)
abstract class OnmBarcodeDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
}