package com.example.onmbarcode.data.equipment

import androidx.room.*
import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.presentation.equipment.Equipment

@Entity(
    foreignKeys = [ForeignKey(
        entity = DeskEntity::class,
        parentColumns = ["id"],
        childColumns = ["deskId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = DeskEntity::class,
        parentColumns = ["id"],
        childColumns = ["previousDeskId"]
    )],
    indices = [Index(value = ["deskId"]), Index(value = ["barcode"], unique = true)]
)
data class EquipmentEntity(
    @PrimaryKey val id: Int,
    val barcode: String,
    val type: String,
    val scanState: Equipment.ScanState,
    val condition: Equipment.EquipmentCondition,
    val scanDate: Long,
    val deskId: Int,
    val previousDeskId: Int
) {
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
