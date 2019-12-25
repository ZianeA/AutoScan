package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.desk.Desk
import dagger.Reusable
import javax.inject.Inject

@Reusable
class DeskWithStatsEntityMapper @Inject constructor() : Mapper<DeskWithStatsEntity, Desk> {
    override fun map(model: DeskWithStatsEntity): Desk {
        return model.run {
            Desk(
                deskEntity.id,
                deskEntity.barcode,
                deskEntity.isScanned,
                deskEntity.scanDate,
                equipmentCount,
                scannedEquipmentCount,
                syncedEquipmentCount
            )
        }
    }

    override fun mapReverse(model: Desk): DeskWithStatsEntity {
        return model.run {
            DeskWithStatsEntity(
                DeskEntity(id, barcode, isScanned, scanDate),
                equipmentCount,
                scannedEquipmentCount,
                syncedEquipmentCount
            )
        }
    }
}