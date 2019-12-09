package com.example.onmbarcode.data.desk

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO add unique constraint to barcode
@Entity
data class DeskEntity(
    @PrimaryKey val id: Int,
    val barcode: String,
    val isScanned: Boolean,
    val scanDate: Long
)