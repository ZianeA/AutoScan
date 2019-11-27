package com.example.onmbarcode.util

import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.data.desk.DeskWithEquipmentsEntity
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment

private const val EQUIPMENT_BARCODE = 12345
private const val EQUIPMENT_TYPE = "clavier"
private val EQUIPMENT_SCAN_STATE = Equipment.ScanState.ScannedAndSynced
private val EQUIPMENT_CONDITION = Equipment.EquipmentCondition.AVERAGE
private const val EQUIPMENT_SCAN_DATE: Long = 1545293705

fun createEquipment(
    barcode: Int = EQUIPMENT_BARCODE,
    type: String = EQUIPMENT_TYPE,
    scanState: Equipment.ScanState = EQUIPMENT_SCAN_STATE,
    condition: Equipment.EquipmentCondition = EQUIPMENT_CONDITION,
    scanDate: Long = EQUIPMENT_SCAN_DATE,
    deskBarcode: String = DESK_BARCODE
) = Equipment(barcode, type, scanState, condition, scanDate, deskBarcode)

fun createEquipmentEntity(
    barcode: Int = EQUIPMENT_BARCODE,
    type: String = EQUIPMENT_TYPE,
    scanState: Equipment.ScanState = EQUIPMENT_SCAN_STATE,
    condition: Equipment.EquipmentCondition = EQUIPMENT_CONDITION,
    scanDate: Long = EQUIPMENT_SCAN_DATE,
    deskBarcode: String = DESK_BARCODE
) = EquipmentEntity(barcode, type, scanState, condition, scanDate, deskBarcode)

private const val DESK_BARCODE = "@CNTM08"
private const val DESK_IS_SCANNED = false
private const val DESK_SCAN_DATE: Long = 1545215405
private val DESK_EQUIPMENTS = listOf(createEquipment())

fun createDesk(
    barcode: String = DESK_BARCODE,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE,
    equipments: List<Equipment> = DESK_EQUIPMENTS
) = Desk(barcode, isScanned, scanDate, equipments)

fun createDeskEntity(
    barcode: String = DESK_BARCODE,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE
) = DeskEntity(barcode, isScanned, scanDate)

fun createDeskWithEquipmentsEntity(
    deskEntity: DeskEntity = createDeskEntity(),
    equipmentEntities: List<EquipmentEntity> = listOf(createEquipmentEntity())
) = DeskWithEquipmentsEntity(deskEntity, equipmentEntities)