package com.meteoalgerie.autoscan.presentation.settings

import com.meteoalgerie.autoscan.data.KeyValueStore
import com.meteoalgerie.autoscan.data.OdooService
import com.meteoalgerie.autoscan.data.PreferencesStringStore
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class SettingsPresenter @Inject constructor(
    private val view: SettingsView,
    private val store: KeyValueStore<String>
) {

    fun start() {
        val serverUrl =
            store.get(PreferencesStringStore.SERVER_URL_KEY, OdooService.URL_SERVER_DEFAULT)
        view.displayServerUrl(serverUrl)
    }

    fun onServerEntered(serverUrl: String) {
        val hasProtocol = serverUrl.startsWith(PROTOCOL_HTTP, true)
                || serverUrl.startsWith(PROTOCOL_HTTPS, true)

        var validUrl: String = serverUrl
        if (!hasProtocol) {
            validUrl = "$PROTOCOL_HTTP$serverUrl"
        }

        store.put(PreferencesStringStore.SERVER_URL_KEY, validUrl)
        view.displayServerUrl(validUrl)
    }

    companion object {
        private const val PROTOCOL_HTTP = "http://"
        private const val PROTOCOL_HTTPS = "https://"
    }
}