package com.example.onmbarcode.presentation.equipment

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

//TODO add scan date
@Entity
data class Equipment(
    @PrimaryKey val barcode: Int,
    val type: String,
    val isScanned: Boolean,
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

    class EquipmentStateConverter {
        @TypeConverter
        fun fromEquipmentStateToString(state: EquipmentState) = state.name

        @TypeConverter
        fun fromStringToEquipmentState(state: String) = EquipmentState.valueOf(state)
    }
}
