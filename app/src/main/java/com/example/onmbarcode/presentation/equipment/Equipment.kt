package com.example.onmbarcode.presentation.equipment

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

//TODO By looking at animate field, I should probably separate presentation model from database model.
@Entity
data class Equipment(
    @PrimaryKey val barcode: Int,
    val type: String,
    val scanState: ScanState,
    val state: EquipmentState,
    val scanDate: Long
) {
    enum class EquipmentState {
        GOOD, AVERAGE, BAD;

        companion object {
            private val values = values();
            fun getByValue(value: Int) = values.first { it.ordinal == value }
        }
    }

    enum class ScanState { ScannedAndSynced, ScannedButNotSynced, NotScanned, PendingScan }

    class EquipmentStateConverter {
        @TypeConverter
        fun fromEquipmentStateToString(state: EquipmentState) = state.name

        @TypeConverter
        fun fromStringToEquipmentState(state: String) = EquipmentState.valueOf(state)
    }

    class ScanStateConverter {
        @TypeConverter
        fun fromScanStateToString(scanState: ScanState) = scanState.name

        @TypeConverter
        fun fromStringToScanState(scanState: String) = ScanState.valueOf(scanState)
    }
}
