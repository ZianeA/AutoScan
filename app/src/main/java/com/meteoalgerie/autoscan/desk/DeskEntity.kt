package com.meteoalgerie.autoscan.desk

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["barcode"], unique = true)])
data class DeskEntity(
    @PrimaryKey val id: Int,
    val barcode: String,
    val isScanned: Boolean,
    val scanDate: Long,
    val isHidden: Boolean
)