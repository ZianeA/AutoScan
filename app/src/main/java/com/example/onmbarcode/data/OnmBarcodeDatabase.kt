package com.example.onmbarcode.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.data.desk.DeskDao
import com.example.onmbarcode.data.equipment.EquipmentDao

@Database(entities = [EquipmentEntity::class, DeskEntity::class], version = 1)
@TypeConverters(
    EquipmentEntity.EquipmentConditionConverter::class,
    EquipmentEntity.ScanStateConverter::class
)
abstract class OnmBarcodeDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun deskDao(): DeskDao
}