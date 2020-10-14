package com.meteoalgerie.autoscan.settings

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate.*
import com.jakewharton.rxrelay2.BehaviorRelay
import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.common.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class SettingsPresenter @Inject constructor(private val storage: PreferenceStorage) {
    val serverUrl = BehaviorRelay.createDefault(storage.serverUrl)
    val theme = BehaviorRelay.createDefault(getDefaultTheme())
    val scanMode = BehaviorRelay.createDefault(getDefaultScanMode())
    val barcodeLength = BehaviorRelay.createDefault(storage.barcodeLength)

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

    fun onChangeTheme(themeMode: ThemeMode) {
        storage.themeMode = themeMode.name
        this.theme.accept(themeMode.text to themeMode.toNightMode())
    }

    fun onChangeScanMode(scanMode: ScanMode) {
        storage.scanMode = scanMode.name
        this.scanMode.accept(scanMode.text)
    }

    fun onChangeBarcodeLength(length: Int) {
        storage.barcodeLength = length
        this.barcodeLength.accept(length)
    }

    private fun getDefaultTheme(): Pair<Int, Int> {
        val themeMode = ThemeMode.valueOf(storage.themeMode)
        return themeMode.text to themeMode.toNightMode()
    }

    private fun getDefaultScanMode() = ScanMode.valueOf(storage.scanMode).text

    companion object {
        private const val PROTOCOL_HTTP = "http://"
        private const val PROTOCOL_HTTPS = "https://"
    }
}

