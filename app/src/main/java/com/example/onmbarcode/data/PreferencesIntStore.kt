package com.example.onmbarcode.data

import android.app.Application
import androidx.preference.PreferenceManager
import javax.inject.Inject

class PreferencesIntStore @Inject constructor(private val app: Application) :
    KeyValueStore<Int> {
    private val preferences
        get() = PreferenceManager.getDefaultSharedPreferences(app)

    override fun get(key: String, defaultValue: Int): Int {
        return preferences.getInt(key, defaultValue)
    }

    override fun put(key: String, value: Int) {
        preferences.edit().putInt(key, value).commit()
    }

    override fun add(key: String, value: Int, defaultValue: Int) {
        put(key, get(key, defaultValue) + value)
    }

    companion object {
        const val EQUIPMENT_COUNT_KEY = "EQUIPMENT_COUNT_KEY"
        const val EQUIPMENT_DOWNLOADED_COUNT_KEY = "EQUIPMENT_DOWNLOADED_COUNT_KEY"
    }
}