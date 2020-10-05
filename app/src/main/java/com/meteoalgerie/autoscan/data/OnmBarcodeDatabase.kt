package com.meteoalgerie.autoscan.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meteoalgerie.autoscan.data.desk.DeskEntity
import com.meteoalgerie.autoscan.data.equipment.Equipment
import com.meteoalgerie.autoscan.data.desk.DeskDao
import com.meteoalgerie.autoscan.data.equipment.EquipmentDao
import com.meteoalgerie.autoscan.data.user.User

@Database(entities = [Equipment::class, DeskEntity::class], version = 1)
@TypeConverters(
    Equipment.EquipmentConditionConverter::class,
    Equipment.ScanStateConverter::class
)
abstract class OnmBarcodeDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun deskDao(): DeskDao
}