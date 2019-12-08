package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.OdooService
import com.example.onmbarcode.data.mapper.OdooDatetimeToUnix
import com.example.onmbarcode.presentation.equipment.Equipment
import dagger.Reusable
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class EquipmentResponseMapper @Inject constructor() :
    Mapper<HashMap<*, *>, Equipment> {
    override fun map(model: HashMap<*, *>): Equipment {
        return Equipment(
            model[ATTRIBUTE_CODE_NAME] as String,
            model[ATTRIBUTE_LIBELLE_NAME] as String,
            Equipment.ScanState.NotScanned, //TODO deal with this. It's probably correct.
            Equipment.EquipmentCondition.getByTranslation(model[ATTRIBUTE_OBSERVATION_NAME] as String),
            OdooDatetimeToUnix(model["date_de_scan"] as String),
            model[ATTRIBUTE_AFF_CODE_NAME] as String
        )
    }

    override fun mapReverse(model: Equipment): HashMap<*, *> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val ATTRIBUTE_CODE_NAME = "code"
        private const val ATTRIBUTE_LIBELLE_NAME = "libelle"
        private const val ATTRIBUTE_OBSERVATION_NAME = "observation"
        private const val ATTRIBUTE_DATE_DE_SCAN_NAME = "date_de_scan"
        private const val ATTRIBUTE_AFF_CODE_NAME = "aff_code"
    }
}