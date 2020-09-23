package com.meteoalgerie.autoscan.data

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesStringSetStore @Inject constructor(app: Application) :
    KeyValueStore<Set<@JvmSuppressWildcards String>> {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(app)
    private lateinit var preferencesRelay: BehaviorRelay<Set<String>>
    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == EQUIPMENT_FILTER_KEY) preferencesRelay.accept(get(key, emptySet()))
    }

    override fun get(key: String, defaultValue: Set<String>): Set<String> {
        return preferences.getStringSet(EQUIPMENT_FILTER_KEY, defaultValue)!!
    }

    override fun observe(key: String, defaultValue: Set<String>): Observable<Set<String>> {
        if (::preferencesRelay.isInitialized.not()) {
            preferencesRelay = BehaviorRelay.createDefault(get(key, defaultValue))
            preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
        }

        return preferencesRelay
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