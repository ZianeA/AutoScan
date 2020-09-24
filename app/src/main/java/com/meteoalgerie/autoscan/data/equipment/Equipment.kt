package com.meteoalgerie.autoscan.data.equipment

import androidx.room.*
import com.meteoalgerie.autoscan.data.desk.DeskEntity

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
data class Equipment(
    @PrimaryKey val id: Int,
    val barcode: String,
    val type: String,
    val scanState: ScanState,
    val condition: EquipmentCondition,
    val scanDate: Long,
    val deskId: Int,
    val previousDeskId: Int
) {
    class EquipmentConditionConverter {
        @TypeConverter
        fun fromEquipmentConditionToString(condition: EquipmentCondition) = condition.name

        @TypeConverter
        fun fromStringToEquipmentCondition(condition: String) =
            EquipmentCondition.valueOf(condition)
    }

    class ScanStateConverter {
        @TypeConverter
        fun fromScanStateToString(scanState: ScanState) = scanState.name

        @TypeConverter
        fun fromStringToScanState(scanState: String) = ScanState.valueOf(scanState)
    }

    enum class EquipmentCondition {
        GOOD, AVERAGE, BAD;

        companion object {
            private val values = values();
            fun getByValue(value: Int) = values.first { it.ordinal == value }
        }
    }

    enum class ScanState { ScannedAndSynced, ScannedButNotSynced, NotScanned }
}
