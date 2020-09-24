package com.meteoalgerie.autoscan.data.equipment

import com.meteoalgerie.autoscan.data.mapper.Mapper
import dagger.Reusable
import javax.inject.Inject

@Reusable
class EquipmentMapper @Inject constructor() :
    Mapper<Equipment, Equipment> {
    override fun map(model: Equipment): Equipment {
        return model.run {
            Equipment(id, barcode, type, scanState, condition, scanDate, deskId, previousDeskId)
        }
    }

    override fun mapReverse(model: Equipment): Equipment {
        return model.run {
            Equipment(
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