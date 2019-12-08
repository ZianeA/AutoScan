package com.example.onmbarcode.data.mapper

import java.text.SimpleDateFormat
import java.util.*

fun OdooDatetimeToUnix(datetime: String): Long {
    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.FRANCE)
    dateFormat.timeZone = TimeZone.getTimeZone(UTC)
    return dateFormat.parse(datetime)!!
        .time
        .div(SECOND_IN_MILLIS)
}

private const val SECOND_IN_MILLIS = 1000
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
private const val UTC = "UTC"