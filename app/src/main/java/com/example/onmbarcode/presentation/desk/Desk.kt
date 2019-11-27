package com.example.onmbarcode.presentation.desk

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.onmbarcode.presentation.equipment.Equipment
import kotlinx.android.parcel.Parcelize

//TODO Desk barcode type should be a string
@Parcelize
data class Desk(
    val barcode: String,
    val isScanned: Boolean,
    val scanDate: Long,
    val equipments: List<Equipment>
) : Parcelable
