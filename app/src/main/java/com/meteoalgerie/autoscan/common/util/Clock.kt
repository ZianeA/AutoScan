package com.meteoalgerie.autoscan.common.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Clock @Inject constructor() {
    val currentTimeSeconds: Long
        get() = System.currentTimeMillis().div(SECOND_IN_MILLIS)

    companion object {
        private const val SECOND_IN_MILLIS = 1000
    }
}