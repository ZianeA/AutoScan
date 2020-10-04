package com.meteoalgerie.autoscan.presentation.settings

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate.*

interface SettingsView {
    fun displayServerUrl(serverUrl: String)
    fun changeTheme(@StringRes name: Int, @NightMode mode: Int)
}
