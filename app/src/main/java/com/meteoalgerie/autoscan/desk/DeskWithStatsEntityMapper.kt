package com.meteoalgerie.autoscan.desk

import dagger.Reusable
import javax.inject.Inject

@Reusable
class DeskWithStatsEntityMapper @Inject constructor() {
    fun map(model: DeskWithStatsEntity): Desk {
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

    fun mapReverse(model: Desk): DeskWithStatsEntity {
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