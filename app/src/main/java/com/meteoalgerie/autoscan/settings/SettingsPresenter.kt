package com.meteoalgerie.autoscan.settings

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate.*
import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.common.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class SettingsPresenter @Inject constructor(private val storage: PreferenceStorage) {

    private val themeNameToMode = mapOf(
        R.string.theme_dark to MODE_NIGHT_YES,
        R.string.theme_light to MODE_NIGHT_NO,
        R.string.theme_follow_os_setting to MODE_NIGHT_FOLLOW_SYSTEM
    )

    val serverUrl = BehaviorRelay.createDefault(storage.serverUrl)
    val theme = BehaviorRelay.createDefault(getTheme())

    fun onServerEntered(serverUrl: String) {
        val hasProtocol = serverUrl.startsWith(PROTOCOL_HTTP, true)
                || serverUrl.startsWith(PROTOCOL_HTTPS, true)

        var validUrl: String = serverUrl
        if (!hasProtocol) {
            validUrl = "$PROTOCOL_HTTP$serverUrl"
        }

        storage.serverUrl = validUrl
        this.serverUrl.accept(validUrl)
    }

    fun onChangeTheme(@StringRes theme: Int) {
        storage.themeMode = themeNameToMode.getMode(theme)
        this.theme.accept(theme to storage.themeMode)
    }

    private fun <K, V> Map<K, V>.getMode(name: K): V = getValue(name)
    private fun <K, V> Map<K, V>.getName(mode: V): K = filterValues { it == mode }.keys.single()
    private fun getTheme(): Pair<Int, Int> {
        val mode = storage.themeMode
        return themeNameToMode.getName(mode) to mode
    }

    companion object {
        private const val PROTOCOL_HTTP = "http://"
        private const val PROTOCOL_HTTPS = "https://"
    }
}