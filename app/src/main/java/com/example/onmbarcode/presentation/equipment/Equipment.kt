package com.example.onmbarcode.presentation.equipment

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Equipment(
    val id: Int,
    val barcode: String,
    val type: String,
    val scanState: ScanState,
    val condition: EquipmentCondition,
    val scanDate: Long,
    val deskId: Int
) : Parcelable {
    enum class EquipmentCondition {
        GOOD, AVERAGE, BAD;

        companion object {
            private val values = values();
            fun getByValue(value: Int) = values.first { it.ordinal == value }
        }
    }

    enum class ScanState { ScannedAndSynced, ScannedButNotSynced, NotScanned }
}
