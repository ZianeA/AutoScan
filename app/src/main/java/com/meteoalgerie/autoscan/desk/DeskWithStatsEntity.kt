package com.meteoalgerie.autoscan.desk

import androidx.room.Embedded

data class DeskWithStatsEntity(
    @Embedded val deskEntity: DeskEntity,
    val equipmentCount: Int,
    val notSyncedEquipmentCount: Int,
    val syncedEquipmentCount: Int
)