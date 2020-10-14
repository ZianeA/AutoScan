package com.meteoalgerie.autoscan.settings

import androidx.annotation.StringRes
import com.meteoalgerie.autoscan.R

enum class ScanMode(@StringRes val text: Int) {
    AUTOMATIC(R.string.scan_mode_automatic), MANUAL(R.string.scan_mode_manual);

    companion object {
        fun getByIndex(index: Int) = values()[index]
    }
}