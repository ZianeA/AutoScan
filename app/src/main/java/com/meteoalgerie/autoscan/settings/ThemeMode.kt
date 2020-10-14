package com.meteoalgerie.autoscan.settings

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.meteoalgerie.autoscan.R

enum class ThemeMode(@StringRes val text: Int) {
    LIGHT(R.string.theme_light), DARK(R.string.theme_dark), SYSTEM(R.string.theme_follow_os_setting);

    fun toNightMode() =
        when (this) {
            DARK -> AppCompatDelegate.MODE_NIGHT_YES
            LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

    companion object {
        fun getByIndex(index: Int) = values()[index]
    }
}