package com.example.onmbarcode.data

import android.app.Application
import androidx.preference.PreferenceManager
import dagger.Reusable
import java.util.Collections.addAll
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class PreferencesStringSetStore @Inject constructor(private val app: Application) :
    KeyValueStore<Set<@JvmSuppressWildcards String>> {
    private val preferences
        get() = PreferenceManager.getDefaultSharedPreferences(app)

    override fun get(key: String, defaultValue: Set<String>): Set<String> {
        return preferences.getStringSet(EQUIPMENT_FILTER_KEY, defaultValue)!!
    }

    override fun put(key: String, value: Set<String>) {
        preferences.edit().putStringSet(EQUIPMENT_FILTER_KEY, value).commit()
    }

    override fun add(key: String, value: Set<String>, defaultValue: Set<String>) {
        put(key, get(key, defaultValue).toMutableSet().apply { addAll(value) })
    }

    override fun remove(key: String, value: Set<String>, defaultValue: Set<String>) {
        put(key, get(key, defaultValue).toMutableSet().apply { removeAll(value) })
    }

    companion object {
        // TODO move to interface
        const val EQUIPMENT_FILTER_KEY = "EQUIPMENT_FILTER_KEY"
    }
}