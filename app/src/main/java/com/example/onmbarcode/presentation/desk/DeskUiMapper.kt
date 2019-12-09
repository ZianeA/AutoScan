package com.example.onmbarcode.presentation.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.equipment.Equipment.*
import dagger.Reusable
import javax.inject.Inject

@Reusable
class DeskUiMapper @Inject constructor() : Mapper<DeskUi, Desk> {
    override fun map(model: DeskUi): Desk {
        return model.run {
            Desk(id, barcode, isScanned, scanDate, equipments)
        }
    }

    override fun mapReverse(model: Desk): DeskUi {
        return model.run {
            DeskUi(
                id,
                barcode,
                isScanned,
                scanDate,
                equipments,
                equipments.filter { it.scanState != ScanState.NotScanned }.size,
                equipments.filter { it.scanState == ScanState.ScannedAndSynced }.size,
                equipments.size
            )
        }
    }
}