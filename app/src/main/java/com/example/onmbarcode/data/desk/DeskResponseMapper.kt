package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.mapper.odooDatetimeToUnix
import dagger.Reusable
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class DeskResponseMapper @Inject constructor() :
    Mapper<HashMap<*, *>, DeskEntity> {
    //TODO add attribute name constants
    override fun map(model: HashMap<*, *>): DeskEntity {
        return DeskEntity(
            model["id"] as Int,
            model["code"] as String,
            false,
            odooDatetimeToUnix(model["date_de_scan"] as String)
        )
    }

    override fun mapReverse(model: DeskEntity): HashMap<*, *> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}