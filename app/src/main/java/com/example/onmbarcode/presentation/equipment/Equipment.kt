package com.example.onmbarcode.presentation.equipment

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Equipment(
    val barcode: String,
    val odooId: Int,
    val type: String,
    val scanState: ScanState,
    val condition: EquipmentCondition,
    val scanDate: Long,
    val deskBarcode: String //TODO remove deskBarcode
) : Parcelable {
    enum class EquipmentCondition {
        GOOD, AVERAGE, BAD;

        companion object {
            private val values = values();
            fun getByValue(value: Int) = values.first { it.ordinal == value }
            // TODO find a better way to handle this!!!!
            fun getByTranslation(value: String) = when (value) {
                "bon" -> GOOD
                "moyen" -> AVERAGE
                "mauvais" -> BAD
                else -> throw IllegalArgumentException("Unknown condition translation")
            }
        }
    }

    enum class ScanState { ScannedAndSynced, ScannedButNotSynced, NotScanned, PendingScan }
}
