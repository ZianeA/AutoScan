package com.example.onmbarcode.presentation.desk

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

//TODO Desk barcode type should be a string
@Parcelize
@Entity
data class Desk(
    @PrimaryKey val barcode: String,
    val isScanned: Boolean,
    val scanDate: Long,
    val scanCount: Int,
    val totalScanCount: Int
) :
    Parcelable
