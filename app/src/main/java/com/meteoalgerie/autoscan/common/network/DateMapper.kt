package com.meteoalgerie.autoscan.common.network

import java.text.SimpleDateFormat
import java.util.*

fun odooDatetimeToUnix(datetime: String): Long {
    val dateFormat = SimpleDateFormat(ODOO_DATE_FORMAT, Locale.FRANCE)
    dateFormat.timeZone = TimeZone.getTimeZone(UTC)
    return dateFormat.parse(datetime)!!
        .time
        .div(SECOND_IN_MILLIS)
}

fun unixToOdooDatetime(unix: Long): String {
    val date = Date(unix * SECOND_IN_MILLIS)
    val dateFormat = SimpleDateFormat(ODOO_DATE_FORMAT, Locale.FRANCE)
    dateFormat.timeZone = TimeZone.getTimeZone(UTC)
    return dateFormat.format(date)
}

private const val SECOND_IN_MILLIS = 1000
private const val ODOO_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
private const val UTC = "UTC"