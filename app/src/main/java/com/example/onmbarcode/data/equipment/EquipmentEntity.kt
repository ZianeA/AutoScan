package com.example.onmbarcode.data.equipment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.presentation.equipment.Equipment

// TODO add lookup tables
@Entity(
    foreignKeys = [ForeignKey(
        entity = DeskEntity::class,
        parentColumns = ["barcode"],
        childColumns = ["deskBarcode"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class EquipmentEntity(
    @PrimaryKey val barcode: String,
    val type: String,
    val scanState: Equipment.ScanState,
    val condition: Equipment.EquipmentCondition,
    val scanDate: Long,
    val deskBarcode: String
){
    class EquipmentConditionConverter {
        @TypeConverter
        fun fromEquipmentConditionToString(condition: Equipment.EquipmentCondition) = condition.name

        @TypeConverter
        fun fromStringToEquipmentCondition(condition: String) =
            Equipment.EquipmentCondition.valueOf(condition)
    }

    class ScanStateConverter {
        @TypeConverter
        fun fromScanStateToString(scanState: Equipment.ScanState) = scanState.name

        @TypeConverter
        fun fromStringToScanState(scanState: String) = Equipment.ScanState.valueOf(scanState)
    }
}
