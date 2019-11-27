package com.example.onmbarcode.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.desk.DeskDao
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.equipment.EquipmentDao

@Database(entities = [Equipment::class, Desk::class], version = 1)
@TypeConverters(Equipment.EquipmentConditionConverter::class, Equipment.ScanStateConverter::class)
abstract class OnmBarcodeDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun deskDao(): DeskDao
}