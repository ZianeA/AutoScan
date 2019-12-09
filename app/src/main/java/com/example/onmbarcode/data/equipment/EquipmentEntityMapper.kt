package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.equipment.Equipment
import dagger.Reusable
import javax.inject.Inject

@Reusable
class EquipmentEntityMapper @Inject constructor() :
    Mapper<EquipmentEntity, Equipment> {
    override fun map(model: EquipmentEntity): Equipment {
        return model.run {
            Equipment(id, barcode, type, scanState, condition, scanDate, deskId)
        }
    }

    override fun mapReverse(model: Equipment): EquipmentEntity {
        return model.run {
            EquipmentEntity(id, barcode, type, scanState, condition, scanDate, deskId)
        }
    }
}