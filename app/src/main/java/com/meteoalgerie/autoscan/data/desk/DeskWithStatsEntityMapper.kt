package com.meteoalgerie.autoscan.data.desk

import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.presentation.desk.Desk
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
                equipmentCount - (notSyncedEquipmentCount + syncedEquipmentCount),
                notSyncedEquipmentCount,
                syncedEquipmentCount,
                deskEntity.isHidden
            )
        }
    }

    override fun mapReverse(model: Desk): DeskWithStatsEntity {
        return model.run {
            DeskWithStatsEntity(
                DeskEntity(id, barcode, isScanned, scanDate, isHidden),
                equipmentCount,
                notSyncedEquipmentCount,
                syncedEquipmentCount
            )
        }
    }
}