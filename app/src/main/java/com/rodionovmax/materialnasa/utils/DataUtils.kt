package com.rodionovmax.materialnasa.utils

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun getDateWithOffset(daysOffset: Int): String {
    val cal: Calendar = Calendar.getInstance()
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    cal.add(Calendar.DATE, -daysOffset)
    val text: String = dateFormat.format(cal.time)
    Log.d("getDate", "Date with offset - $daysOffset $text")
    return text
}

fun getTimestamp(): String {
    return System.currentTimeMillis().toString()
}

fun timestampToDate(timestamp: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val netDate = Date(timestamp.toLong())
    return sdf.format(netDate)
}
