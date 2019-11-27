package com.example.onmbarcode.data.desk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeskEntity(
    @PrimaryKey val barcode: String,
    val isScanned: Boolean,
    val scanDate: Long
)