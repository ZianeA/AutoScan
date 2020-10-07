package com.meteoalgerie.autoscan.util

import com.meteoalgerie.autoscan.desk.DeskEntity
import com.meteoalgerie.autoscan.desk.Desk
import kotlin.collections.HashMap

private const val EQUIPMENT_ID = 101
private const val EQUIPMENT_BARCODE = "12345"
private const val EQUIPMENT_TYPE = "clavier"
private val EQUIPMENT_SCAN_STATE = Equipment.ScanState.ScannedAndSynced
private val EQUIPMENT_CONDITION = Equipment.EquipmentCondition.AVERAGE
private const val EQUIPMENT_CONDITION_FRENCH = "moyen"
private const val EQUIPMENT_SCAN_DATE = 1545293705L
private const val EQUIPMENT_SCAN_DATE_STRING = "2018-12-20 08:15:05"

fun createEquipment(
    id: Int = EQUIPMENT_ID,
    barcode: String = EQUIPMENT_BARCODE,
    type: String = EQUIPMENT_TYPE,
    scanState: Equipment.ScanState = EQUIPMENT_SCAN_STATE,
    condition: Equipment.EquipmentCondition = EQUIPMENT_CONDITION,
    scanDate: Long = EQUIPMENT_SCAN_DATE,
    deskId: Int = DESK_ID,
    previousDeskId: Int = DESK_ID
) = Equipment(id, barcode, type, scanState, condition, scanDate, deskId, previousDeskId)

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
private const val DESK_EQUIPMENT_COUNT = 30
private const val DESK_NOT_SCANNED_EQUIPMENT_COUNT = 10
private const val DESK_NOT_SYNCED_EQUIPMENT_COUNT = 10
private const val DESK_SYNCED_EQUIPMENT_COUNT = 10
private const val DESK_IS_HIDDEN = false

fun createDeskEntity(
    id: Int = DESK_ID,
    barcode: String = DESK_BARCODE,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE,
    isHidden: Boolean = DESK_IS_HIDDEN
) = DeskEntity(id, barcode, isScanned, scanDate, isHidden)

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

fun createDesk(
    id: Int = DESK_ID,
    barcode: String = DESK_BARCODE,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE,
    equipmentCount: Int = DESK_EQUIPMENT_COUNT,
    notScannedEquipmentCount: Int = DESK_NOT_SCANNED_EQUIPMENT_COUNT,
    notSyncedEquipmentCount: Int = DESK_NOT_SYNCED_EQUIPMENT_COUNT,
    syncedEquipmentCount: Int = DESK_SYNCED_EQUIPMENT_COUNT,
    isHidden: Boolean = DESK_IS_HIDDEN
) = Desk(
    id,
    barcode,
    isScanned,
    scanDate,
    equipmentCount,
    notScannedEquipmentCount,
    notSyncedEquipmentCount,
    syncedEquipmentCount,
    isHidden
)