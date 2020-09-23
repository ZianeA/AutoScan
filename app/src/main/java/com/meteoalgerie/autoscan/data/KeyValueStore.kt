package com.meteoalgerie.autoscan.data

import io.reactivex.Observable

interface KeyValueStore<T> {
    fun get(key: String, defaultValue: T): T
    fun observe(key: String, defaultValue: T): Observable<T>
    fun put(key: String, value: T)
    fun add(key: String, value: T, defaultValue: T)
    fun remove(key: String, value: T, defaultValue: T)
}