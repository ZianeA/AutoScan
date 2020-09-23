package com.meteoalgerie.autoscan.data.equipment

import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.presentation.equipment.Equipment
import dagger.Reusable
import javax.inject.Inject

@Reusable
class EquipmentEntityMapper @Inject constructor() :
    Mapper<EquipmentEntity, Equipment> {
    override fun map(model: EquipmentEntity): Equipment {
        return model.run {
            Equipment(id, barcode, type, scanState, condition, scanDate, deskId, previousDeskId)
        }
    }

    override fun mapReverse(model: Equipment): EquipmentEntity {
        return model.run {
            EquipmentEntity(
                id,
                barcode,
                type,
                scanState,
                condition,
                scanDate,
                deskId,
                previousDeskId
            )
        }
    }
}