package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.mapper.odooDatetimeToUnix
import com.example.onmbarcode.data.mapper.unixToOdooDatetime
import com.example.onmbarcode.presentation.equipment.Equipment.*
import dagger.Reusable
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class EquipmentResponseMapper @Inject constructor() :
    Mapper<HashMap<*, *>, EquipmentEntity> {
    override fun map(model: HashMap<*, *>): EquipmentEntity {
        return EquipmentEntity(
            model[ATTRIBUTE_ID] as Int,
            model[ATTRIBUTE_CODE] as String,
            model[ATTRIBUTE_LIBELLE] as String,
            if (model[ATTRIBUTE_SCANNE] as Boolean) ScanState.ScannedAndSynced else ScanState.NotScanned,
            translateCondition(model[ATTRIBUTE_OBSERVATION] as String),
            odooDatetimeToUnix(model[ATTRIBUTE_DATE_DE_SCAN] as String),
            (model[ATTRIBUTE_CODE_AFF] as Array<*>)[0] as Int,
            (model[ATTRIBUTE_CODE_AFF] as Array<*>)[0] as Int
        )
    }

    override fun mapReverse(model: EquipmentEntity): HashMap<*, *> {
        return model.run {
            hashMapOf<Any, Any>(
                ATTRIBUTE_OBSERVATION to translateCondition(condition),
                ATTRIBUTE_DATE_DE_SCAN to unixToOdooDatetime(scanDate),
                ATTRIBUTE_CODE_AFF to deskId,
                ATTRIBUTE_SCANNE to if (scanState == ScanState.ScannedAndSynced) true
                else throw IllegalArgumentException("Can't send unscanned equipment to server") // TODO is this correct?
            )
        }
    }

    companion object {
        const val ATTRIBUTE_ID = "id"
        const val ATTRIBUTE_CODE = "code"
        const val ATTRIBUTE_LIBELLE = "libelle"
        const val ATTRIBUTE_OBSERVATION = "observation"
        const val ATTRIBUTE_DATE_DE_SCAN = "date_scan"
        const val ATTRIBUTE_CODE_AFF = "code_aff"
        const val ATTRIBUTE_SCANNE = "scanne"
    }

    fun translateCondition(condition: String) = when (condition.toUpperCase(Locale.FRENCH)) {
        "BON" -> EquipmentCondition.GOOD
        "MOYEN" -> EquipmentCondition.AVERAGE
        "MAUVAIS" -> EquipmentCondition.BAD
        else -> throw IllegalArgumentException("Unknown equipment condition")
    }

    fun translateCondition(condition: EquipmentCondition) = when (condition) {
        EquipmentCondition.GOOD -> "bon"
        EquipmentCondition.AVERAGE -> "moyen"
        EquipmentCondition.BAD -> "mauvais"
    }
}