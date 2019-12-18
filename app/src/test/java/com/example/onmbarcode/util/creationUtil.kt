package com.example.onmbarcode.util

import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.data.desk.DeskWithEquipmentsEntity
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.desk.DeskUi
import com.example.onmbarcode.presentation.equipment.Equipment
import kotlin.collections.HashMap

private const val EQUIPMENT_ID = 101
private const val EQUIPMENT_BARCODE = "12345"
private const val EQUIPMENT_TYPE = "clavier"
private val EQUIPMENT_SCAN_STATE = Equipment.ScanState.ScannedAndSynced
private val EQUIPMENT_CONDITION = Equipment.EquipmentCondition.AVERAGE
private val EQUIPMENT_CONDITION_FRENCH = "moyen"
private const val EQUIPMENT_SCAN_DATE: Long = 1545293705
private const val EQUIPMENT_SCAN_DATE_STRING = "2018-12-20 08:15:05"

fun createEquipment(
    id: Int = EQUIPMENT_ID,
    barcode: String = EQUIPMENT_BARCODE,
    type: String = EQUIPMENT_TYPE,
    scanState: Equipment.ScanState = EQUIPMENT_SCAN_STATE,
    condition: Equipment.EquipmentCondition = EQUIPMENT_CONDITION,
    scanDate: Long = EQUIPMENT_SCAN_DATE,
    deskBarcode: Int = DESK_ID
) = Equipment(id, barcode, type, scanState, condition, scanDate, deskBarcode)

fun createEquipmentEntity(
    id: Int = EQUIPMENT_ID,
    barcode: String = EQUIPMENT_BARCODE,
    type: String = EQUIPMENT_TYPE,
    scanState: Equipment.ScanState = EQUIPMENT_SCAN_STATE,
    condition: Equipment.EquipmentCondition = EQUIPMENT_CONDITION,
    scanDate: Long = EQUIPMENT_SCAN_DATE,
    deskBarcode: Int = DESK_ID
) = EquipmentEntity(id, barcode, type, scanState, condition, scanDate, deskBarcode)

fun createEquipmentResponse(
    id: Int = EQUIPMENT_ID,
    barcode: String = EQUIPMENT_BARCODE,
    type: String = EQUIPMENT_TYPE,
    condition: String = EQUIPMENT_CONDITION_FRENCH,
    scanDate: String = EQUIPMENT_SCAN_DATE_STRING,
    deskId: Int = DESK_ID
): HashMap<*, *> {
    return hashMapOf<Any, Any>(
        "id" to id,
        "code" to barcode,
        "libelle" to type,
        "observation" to condition,
        "date_de_scan" to scanDate,
        "aff" to arrayOf<Any>(deskId)
    )
}

private const val DESK_ID = 202
private const val DESK_BARCODE = "CNTM08"
private const val DESK_IS_SCANNED = false
private const val DESK_SCAN_DATE: Long = 1545215405
private const val DESK_SCAN_DATE_STRING = "2018-12-19 10:30:05"
private val DESK_EQUIPMENTS = listOf(createEquipment())

fun createDesk(
    id: Int = DESK_ID,
    barcode: String = DESK_BARCODE,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE,
    equipments: List<Equipment> = DESK_EQUIPMENTS
) = Desk(id, barcode, isScanned, scanDate, equipments)

fun createDeskEntity(
    id: Int = DESK_ID,
    barcode: String = DESK_BARCODE,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE
) = DeskEntity(id, barcode, isScanned, scanDate)

fun createDeskWithEquipmentsEntity(
    deskEntity: DeskEntity = createDeskEntity(),
    equipmentEntities: List<EquipmentEntity> = listOf(createEquipmentEntity())
) = DeskWithEquipmentsEntity(deskEntity, equipmentEntities)

fun createDeskResponse(
    id: Int = DESK_ID,
    barcode: String = DESK_BARCODE,
    scanDate: String = DESK_SCAN_DATE_STRING,
    equipments: Array<*> = arrayOf(createEquipmentResponse())
): HashMap<*, *> {
    return hashMapOf<Any, Any>(
        "id" to id,
        "code" to barcode,
        "date_de_scan" to scanDate,
        "equipment" to equipments
    )
}

fun createDeskUi(
    id: Int = DESK_ID,
    barcode: String = DESK_BARCODE,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE,
    equipments: List<Equipment> = DESK_EQUIPMENTS,
    scannedEquipmentCount: Int = equipments.filter { it.scanState != Equipment.ScanState.NotScanned }.size,
    syncedEquipmentCount: Int = equipments.filter { it.scanState == Equipment.ScanState.ScannedAndSynced }.size,
    equipmentsCount: Int = equipments.size
) = DeskUi(
    id,
    barcode,
    isScanned,
    scanDate,
    equipments,
    scannedEquipmentCount,
    syncedEquipmentCount,
    equipmentsCount
)