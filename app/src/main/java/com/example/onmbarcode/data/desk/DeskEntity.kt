package com.example.onmbarcode.data.desk

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO add unique constraint to odooId
@Entity
data class DeskEntity(
    @PrimaryKey val barcode: String,
    val odooId: Int,
    val isScanned: Boolean,
    val scanDate: Long
)