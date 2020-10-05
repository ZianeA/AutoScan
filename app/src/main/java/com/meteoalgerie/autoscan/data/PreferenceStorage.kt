package com.meteoalgerie.autoscan.data

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.meteoalgerie.autoscan.data.equipment.Equipment
import com.meteoalgerie.autoscan.data.user.User
import hu.autsoft.krate.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceStorage @Inject constructor(context: Application) : SimpleKrate(context) {
    private var userId: Int? by intPref("user_id")
    private var userPassword: String? by stringPref("user_password")

    var user: User?
        get() {
            return safeLet(userId, userPassword) { id, password ->
                User(id, password)
            }
        }
        set(value) {
            userId = value?.id
            userPassword = value?.password
        }

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


    private inline fun <T1 : Any, T2 : Any, R : Any> safeLet(
        p1: T1?,
        p2: T2?,
        block: (T1, T2) -> R?
    ): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }
}