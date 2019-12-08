package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.OdooService
import com.example.onmbarcode.data.mapper.OdooDatetimeToUnix
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment
import dagger.Reusable
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class DeskResponseMapper @Inject constructor() :
    Mapper<HashMap<*, *>, DeskEntity> {
    override fun map(model: HashMap<*, *>): DeskEntity {
        return DeskEntity(
            model["code"] as String,
            false,
            OdooDatetimeToUnix(model["date_de_scan"] as String)/*,
            (model["equipments"] as Array<*>).map { equipmentMapper.map(it as HashMap<*, *>) }*/
        )
    }

    override fun mapReverse(model: DeskEntity): HashMap<*, *> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}