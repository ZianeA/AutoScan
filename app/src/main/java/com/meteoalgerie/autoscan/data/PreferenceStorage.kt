package com.meteoalgerie.autoscan.data

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.meteoalgerie.autoscan.data.equipment.Equipment
import hu.autsoft.krate.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceStorage @Inject constructor(context: Application) : SimpleKrate(context) {
    var serverUrl: String by stringPref("server_url", "http://192.168.0.177:8069")
    var equipmentCount: Int by intPref("equipment_count", 0)
    var downloadedEquipmentCount: Int by intPref("downloaded_equipment_count", 0)
    var equipmentFilter: Set<String> by stringSetPref(
        "equipment_filter",
        setOf(
            Equipment.ScanState.ScannedAndSynced.name,
            Equipment.ScanState.ScannedButNotSynced.name,
            Equipment.ScanState.NotScanned.name
        )
    )
    var themeMode: Int by intPref("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}