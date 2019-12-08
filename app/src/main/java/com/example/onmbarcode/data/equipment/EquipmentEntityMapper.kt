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
            Equipment(barcode, odooId, type, scanState, condition, scanDate, deskBarcode)
        }
    }

    override fun mapReverse(model: Equipment): EquipmentEntity {
        return model.run {
            EquipmentEntity(barcode, odooId, type, scanState, condition, scanDate, deskBarcode)
        }
    }
}