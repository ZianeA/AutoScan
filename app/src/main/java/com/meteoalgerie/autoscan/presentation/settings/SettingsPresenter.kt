package com.meteoalgerie.autoscan.presentation.settings

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate.*
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class SettingsPresenter @Inject constructor(
    private val view: SettingsView,
    private val storage: PreferenceStorage
) {

    fun start() {
        view.displayServerUrl(storage.serverUrl)
        val mode = storage.themeMode
        view.changeTheme(themeNameToMode.getName(mode), mode)
    }

    fun onServerEntered(serverUrl: String) {
        val hasProtocol = serverUrl.startsWith(PROTOCOL_HTTP, true)
                || serverUrl.startsWith(PROTOCOL_HTTPS, true)

        var validUrl: String = serverUrl
        if (!hasProtocol) {
            validUrl = "$PROTOCOL_HTTP$serverUrl"
        }

        storage.serverUrl = validUrl
        view.displayServerUrl(storage.serverUrl)
    }

    fun onChangeTheme(@StringRes theme: Int) {
        storage.themeMode = themeNameToMode.getMode(theme)
        view.changeTheme(theme, storage.themeMode)
    }

    private val themeNameToMode = mapOf(
        R.string.theme_dark to MODE_NIGHT_YES,
        R.string.theme_light to MODE_NIGHT_NO,
        R.string.theme_follow_os_setting to MODE_NIGHT_FOLLOW_SYSTEM
    )

    private fun <K, V> Map<K, V>.getMode(name: K): V = getValue(name)
    private fun <K, V> Map<K, V>.getName(mode: V): K = filterValues { it == mode }.keys.single()

    companion object {
        private const val PROTOCOL_HTTP = "http://"
        private const val PROTOCOL_HTTPS = "https://"
    }
}