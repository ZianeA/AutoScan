package com.meteoalgerie.autoscan.presentation.desk

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Desk(
    val id: Int,
    val barcode: String,
    val isScanned: Boolean,
    val scanDate: Long,
    val equipmentCount: Int,
    val notScannedEquipmentCount: Int,
    val notSyncedEquipmentCount: Int,
    val syncedEquipmentCount: Int,
    val isHidden: Boolean
) : Parcelable
