package com.meteoalgerie.autoscan.data.mapper

interface Mapper<T, R> {
    fun map(model: T): R
    fun mapReverse(model: R): T
}