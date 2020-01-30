package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.data.mapper.odooDatetimeToUnix
import com.example.onmbarcode.presentation.util.Clock
import dagger.Reusable
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class DeskResponseMapper @Inject constructor(private val clock: Clock) :
    Mapper<HashMap<*, *>, DeskEntity> {
    override fun map(model: HashMap<*, *>): DeskEntity {
        return DeskEntity(
            model[ATTRIBUTE_ID_NAME] as Int,
            model[ATTRIBUTE_CODE_BUREAU_NAME] as String,
            false,
            clock.currentTimeSeconds,
            false
        )
    }

    override fun mapReverse(model: DeskEntity): HashMap<*, *> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val ATTRIBUTE_ID_NAME = "id"
        const val ATTRIBUTE_CODE_BUREAU_NAME = "code_bureau"
    }
}