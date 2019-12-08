package com.example.onmbarcode.presentation.desk

import android.os.Parcelable
import com.example.onmbarcode.presentation.equipment.Equipment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeskUi(
    val barcode: String,
    val odooId: Int,
    val isScanned: Boolean,
    val scanDate: Long,
    val equipments: List<Equipment>,
    val scannedEquipmentCount: Int,
    val syncedEquipmentCount: Int,
    val equipmentsCount: Int
) : Parcelable