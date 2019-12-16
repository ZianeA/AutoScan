package com.example.onmbarcode.data

import android.app.Application
import dagger.Reusable
import javax.inject.Inject

@Reusable
class PreferencesKeyValueStore @Inject constructor(app: Application) : KeyValueStore<String> {
    private val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(app)

    override fun get(key: String, defaultValue: String): String {
        return preferences.getString(key, defaultValue)!!
    }

    override fun put(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    companion object {
        const val SERVER_URL_KEY = "SERVER_URL"
    }
}