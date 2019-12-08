package com.example.onmbarcode.presentation.desk

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.onmbarcode.presentation.equipment.Equipment
import kotlinx.android.parcel.Parcelize

data class Desk(
    val barcode: String,
    val odooId: Int,
    val isScanned: Boolean,
    val scanDate: Long,
    val equipments: List<Equipment>
)
