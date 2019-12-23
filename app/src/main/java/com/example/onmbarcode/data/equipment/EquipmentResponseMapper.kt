package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.mapper.odooDatetimeToUnix
import com.example.onmbarcode.data.mapper.unixToOdooDatetime
import com.example.onmbarcode.presentation.equipment.Equipment
import dagger.Reusable
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class EquipmentResponseMapper @Inject constructor() :
    Mapper<HashMap<*, *>, Equipment> {
    override fun map(model: HashMap<*, *>): Equipment {
        return Equipment(
            model[ATTRIBUTE_ID_NAME] as Int,
            model[ATTRIBUTE_CODE_NAME] as String,
            model[ATTRIBUTE_LIBELLE_NAME] as String,
            Equipment.ScanState.NotScanned, //TODO deal with this. It's probably correct.
            translateCondition(model[ATTRIBUTE_OBSERVATION_NAME] as String),
            odooDatetimeToUnix(model[ATTRIBUTE_DATE_DE_SCAN_NAME] as String),
            (model[ATTRIBUTE_CODE_AFF_NAME] as Array<*>)[0] as Int,
            (model[ATTRIBUTE_CODE_AFF_NAME] as Array<*>)[0] as Int
        )
    }

    override fun mapReverse(model: Equipment): HashMap<*, *> {
        return model.run {
            hashMapOf<Any, Any>(
                ATTRIBUTE_OBSERVATION_NAME to translateCondition(condition),
                ATTRIBUTE_DATE_DE_SCAN_NAME to unixToOdooDatetime(scanDate),
                ATTRIBUTE_CODE_AFF_NAME to deskId,
                ATTRIBUTE_SCANNE to if (scanState == Equipment.ScanState.ScannedAndSynced) true
                else throw IllegalArgumentException("Can't send unscanned equipment to server") // TODO is this correct?
            )
        }
    }

    companion object {
        const val ATTRIBUTE_ID_NAME = "id"
        const val ATTRIBUTE_CODE_NAME = "code"
        const val ATTRIBUTE_LIBELLE_NAME = "libelle"
        const val ATTRIBUTE_OBSERVATION_NAME = "observation"
        const val ATTRIBUTE_DATE_DE_SCAN_NAME = "date_scan"
        const val ATTRIBUTE_CODE_AFF_NAME = "code_aff"
        const val ATTRIBUTE_SCANNE = "scanne"
    }

    fun translateCondition(condition: String) = when (condition.toUpperCase(Locale.FRENCH)) {
        "BON" -> Equipment.EquipmentCondition.GOOD
        "MOYEN" -> Equipment.EquipmentCondition.AVERAGE
        "MAUVAIS" -> Equipment.EquipmentCondition.BAD
        else -> throw IllegalArgumentException("Unknown equipment condition")
    }

    fun translateCondition(condition: Equipment.EquipmentCondition) = when (condition) {
        Equipment.EquipmentCondition.GOOD -> "bon"
        Equipment.EquipmentCondition.AVERAGE -> "moyen"
        Equipment.EquipmentCondition.BAD -> "mauvais"
    }
}