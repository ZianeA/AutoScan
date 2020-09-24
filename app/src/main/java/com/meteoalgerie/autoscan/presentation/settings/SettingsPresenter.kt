package com.meteoalgerie.autoscan.presentation.settings

import com.meteoalgerie.autoscan.data.OdooService
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

    companion object {
        private const val PROTOCOL_HTTP = "http://"
        private const val PROTOCOL_HTTPS = "https://"
    }
}