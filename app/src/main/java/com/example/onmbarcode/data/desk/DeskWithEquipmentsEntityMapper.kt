package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.Mapper
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment
import dagger.Reusable
import javax.inject.Inject

@Reusable
class DeskWithEquipmentsEntityMapper @Inject constructor(private val equipmentMapper: Mapper<EquipmentEntity, Equipment>) :
    Mapper<DeskWithEquipmentsEntity, Desk> {
    override fun map(model: DeskWithEquipmentsEntity): Desk {
        return model.run {
            Desk(
                deskEntity.barcode,
                deskEntity.isScanned,
                deskEntity.scanDate,
                equipmentEntities.map(equipmentMapper::map)
            )
        }
    }

    override fun mapReverse(model: Desk): DeskWithEquipmentsEntity {
        return model.run {
            DeskWithEquipmentsEntity(
                DeskEntity(barcode, isScanned, scanDate),
                equipments.map(equipmentMapper::mapReverse)
            )
        }
    }
}