package com.example.onmbarcode.data.desk

import androidx.room.Embedded
import androidx.room.Relation
import com.example.onmbarcode.data.equipment.EquipmentEntity

data class DeskWithEquipmentsEntity(
    @Embedded val deskEntity: DeskEntity,
    @Relation(
        parentColumn = "barcode",
        entityColumn = "deskBarcode"
    ) val equipmentEntities: List<EquipmentEntity>
)