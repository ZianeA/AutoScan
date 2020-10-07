package com.meteoalgerie.autoscan.desk

import com.meteoalgerie.autoscan.common.util.Clock
import dagger.Reusable
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class DeskResponseMapper @Inject constructor(private val clock: Clock){
    fun map(model: HashMap<*, *>): DeskEntity {
        return DeskEntity(
            model[ATTRIBUTE_ID_NAME] as Int,
            model[ATTRIBUTE_CODE_BUREAU_NAME] as String,
            false,
            clock.currentTimeSeconds,
            false
        )
    }

    companion object {
        const val ATTRIBUTE_ID_NAME = "id"
        const val ATTRIBUTE_CODE_BUREAU_NAME = "code_bureau"
    }
}