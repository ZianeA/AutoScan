package com.example.onmbarcode.data

interface KeyValueStore<T> {
    fun get(key: String, defaultValue: T): T
    fun put(key: String, value: T)
    fun add(key: String, value: T, defaultValue: T)
    fun remove(key: String, value: T, defaultValue: T)
}