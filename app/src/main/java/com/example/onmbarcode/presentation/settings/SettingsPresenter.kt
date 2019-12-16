package com.example.onmbarcode.presentation.settings

import com.example.onmbarcode.data.KeyValueStore
import com.example.onmbarcode.data.OdooService
import com.example.onmbarcode.data.PreferencesKeyValueStore
import com.example.onmbarcode.presentation.di.FragmentScope
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import javax.inject.Inject

@FragmentScope
class SettingsPresenter @Inject constructor(
    private val view: SettingsView,
    private val store: KeyValueStore<String>
) {

    fun start() {
        val serverUrl =
            store.get(PreferencesKeyValueStore.SERVER_URL_KEY, OdooService.URL_SERVER_DEFAULT)
        view.displayServerUrl(serverUrl)
    }

    fun onServerEntered(serverUrl: String) {
        val hasProtocol = serverUrl.startsWith(PROTOCOL_HTTP, true)
                || serverUrl.startsWith(PROTOCOL_HTTPS, true)

        var validUrl: String = serverUrl
        if (!hasProtocol) {
            validUrl = "$PROTOCOL_HTTP$serverUrl"
        }

        store.put(PreferencesKeyValueStore.SERVER_URL_KEY, validUrl)
        view.displayServerUrl(validUrl)
    }

    companion object {
        private const val PROTOCOL_HTTP = "http://"
        private const val PROTOCOL_HTTPS = "https://"
    }
}