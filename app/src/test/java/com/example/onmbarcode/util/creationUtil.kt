package com.example.onmbarcode.util

import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.data.desk.DeskWithEquipmentsEntity
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.desk.DeskUi
import com.example.onmbarcode.presentation.equipment.Equipment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

private const val EQUIPMENT_BARCODE = "12345"
private const val EQUIPMENT_ODOO_ID = 101
private const val EQUIPMENT_TYPE = "clavier"
private val EQUIPMENT_SCAN_STATE = Equipment.ScanState.ScannedAndSynced
private val EQUIPMENT_CONDITION = Equipment.EquipmentCondition.AVERAGE
private val EQUIPMENT_CONDITION_FRENCH = "moyen"
private const val EQUIPMENT_SCAN_DATE: Long = 1545293705
private const val EQUIPMENT_SCAN_DATE_STRING = "2018-12-20 08:15:05"

fun createEquipment(
    barcode: String = EQUIPMENT_BARCODE,
    odooId: Int = EQUIPMENT_ODOO_ID,
    type: String = EQUIPMENT_TYPE,
    scanState: Equipment.ScanState = EQUIPMENT_SCAN_STATE,
    condition: Equipment.EquipmentCondition = EQUIPMENT_CONDITION,
    scanDate: Long = EQUIPMENT_SCAN_DATE,
    deskBarcode: String = DESK_BARCODE
) = Equipment(barcode, odooId, type, scanState, condition, scanDate, deskBarcode)

fun createEquipmentEntity(
    barcode: String = EQUIPMENT_BARCODE,
    odooId: Int = EQUIPMENT_ODOO_ID,
    type: String = EQUIPMENT_TYPE,
    scanState: Equipment.ScanState = EQUIPMENT_SCAN_STATE,
    condition: Equipment.EquipmentCondition = EQUIPMENT_CONDITION,
    scanDate: Long = EQUIPMENT_SCAN_DATE,
    deskBarcode: String = DESK_BARCODE
) = EquipmentEntity(barcode, odooId, type, scanState, condition, scanDate, deskBarcode)

fun createEquipmentResponse(
    barcode: String = EQUIPMENT_BARCODE,
    odooId: Int = EQUIPMENT_ODOO_ID,
    type: String = EQUIPMENT_TYPE,
    condition: String = EQUIPMENT_CONDITION_FRENCH,
    scanDate: String = EQUIPMENT_SCAN_DATE_STRING,
    deskBarcode: String = DESK_BARCODE
): HashMap<*, *> {
    return hashMapOf<Any, Any>(
        "code" to barcode,
        "id" to odooId,
        "libelle" to type,
        "observation" to condition,
        "date_de_scan" to scanDate,
        "aff_code" to deskBarcode
    )
}

private const val DESK_BARCODE = "CNTM08"
private const val DESK_ODOO_ID = 202
private const val DESK_IS_SCANNED = false
private const val DESK_SCAN_DATE: Long = 1545215405
private const val DESK_SCAN_DATE_STRING = "2018-12-19 10:30:05"
private val DESK_EQUIPMENTS = listOf(createEquipment())

fun createDesk(
    barcode: String = DESK_BARCODE,
    odooId: Int = DESK_ODOO_ID,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE,
    equipments: List<Equipment> = DESK_EQUIPMENTS
) = Desk(barcode, odooId, isScanned, scanDate, equipments)

fun createDeskEntity(
    barcode: String = DESK_BARCODE,
    odooId: Int = DESK_ODOO_ID,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE
) = DeskEntity(barcode, odooId, isScanned, scanDate)

fun createDeskWithEquipmentsEntity(
    deskEntity: DeskEntity = createDeskEntity(),
    equipmentEntities: List<EquipmentEntity> = listOf(createEquipmentEntity())
) = DeskWithEquipmentsEntity(deskEntity, equipmentEntities)

fun createDeskResponse(
    barcode: String = DESK_BARCODE,
    odooId: Int = DESK_ODOO_ID,
    scanDate: String = DESK_SCAN_DATE_STRING,
    equipments: Array<*> = arrayOf(createEquipmentResponse())
): HashMap<*, *> {
    return hashMapOf<Any, Any>(
        "code" to barcode,
        "id" to odooId,
        "date_de_scan" to scanDate,
        "equipments" to equipments
    )
}

fun createDeskUi(
    barcode: String = DESK_BARCODE,
    odooId: Int = DESK_ODOO_ID,
    isScanned: Boolean = DESK_IS_SCANNED,
    scanDate: Long = DESK_SCAN_DATE,
    equipments: List<Equipment> = DESK_EQUIPMENTS,
    scannedEquipmentCount: Int = equipments.filter { it.scanState != Equipment.ScanState.NotScanned }.size,
    syncedEquipmentCount: Int = equipments.filter { it.scanState == Equipment.ScanState.ScannedAndSynced }.size,
    equipmentsCount: Int = equipments.size
) = DeskUi(
    barcode,
    odooId,
    isScanned,
    scanDate,
    equipments,
    scannedEquipmentCount,
    syncedEquipmentCount,
    equipmentsCount
)