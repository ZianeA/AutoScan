package com.meteoalgerie.autoscan.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meteoalgerie.autoscan.desk.DeskEntity
import com.meteoalgerie.autoscan.equipment.Equipment
import com.meteoalgerie.autoscan.desk.DeskDao
import com.meteoalgerie.autoscan.equipment.EquipmentDao

@Database(entities = [Equipment::class, DeskEntity::class], version = 1)
@TypeConverters(
    Equipment.EquipmentConditionConverter::class,
    Equipment.ScanStateConverter::class
)
abstract class OnmBarcodeDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun deskDao(): DeskDao
}