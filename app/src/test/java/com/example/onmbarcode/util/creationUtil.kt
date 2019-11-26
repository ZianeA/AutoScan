package com.example.onmbarcode.util

import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment

fun createEquipment(
    barcode: Int = 12345,
    type: String = "clavier",
    scanState: Equipment.ScanState = Equipment.ScanState.ScannedAndSynced,
    condition: Equipment.EquipmentCondition = Equipment.EquipmentCondition.AVERAGE,
    scanDate: Long = System.currentTimeMillis(),
    deskBarcode: String = DESK_ID.toString()
) = Equipment(barcode, type, scanState, condition, scanDate, deskBarcode)

private const val DESK_ID = 101
fun createDesk(barcode: Int = DESK_ID, scanCount: Int = 202, totalScanCount: Int = 303) =
    Desk(barcode, scanCount, totalScanCount)