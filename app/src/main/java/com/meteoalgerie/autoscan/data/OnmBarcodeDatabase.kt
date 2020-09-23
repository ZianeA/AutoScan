package com.meteoalgerie.autoscan.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meteoalgerie.autoscan.data.desk.DeskEntity
import com.meteoalgerie.autoscan.data.equipment.EquipmentEntity
import com.meteoalgerie.autoscan.data.desk.DeskDao
import com.meteoalgerie.autoscan.data.equipment.EquipmentDao
import com.meteoalgerie.autoscan.data.user.UserDao
import com.meteoalgerie.autoscan.data.user.UserEntity

@Database(entities = [EquipmentEntity::class, DeskEntity::class, UserEntity::class], version = 1)
@TypeConverters(
    EquipmentEntity.EquipmentConditionConverter::class,
    EquipmentEntity.ScanStateConverter::class
)
abstract class OnmBarcodeDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun deskDao(): DeskDao
    abstract fun userDao(): UserDao
}