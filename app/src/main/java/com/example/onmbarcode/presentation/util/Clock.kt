package com.example.onmbarcode.presentation.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Clock @Inject constructor() {
    val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}