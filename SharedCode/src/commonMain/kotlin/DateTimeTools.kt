package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.parse


const val queryOffsetInSeconds = 6 * 60
const val apiDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"

fun apiTimeToDateTime(apiTime: String): DateTime {
    val localTimeFormat = DateFormat(apiDateTimeFormat)
    val timeWithoutTimeZone = apiTime.split("+").first()
    val localTime = localTimeFormat.parse(timeWithoutTimeZone)
    return localTime.local
}

fun extractSimpleTime(time: String): String {
    return apiTimeToDateTime(time).toString("HH:mm")
}

fun convertToHoursAndMinutes(journeyDurationInMinutes: Int): String {
    return "${journeyDurationInMinutes / 60}h ${journeyDurationInMinutes % 60}min"
}

fun getEarliestSearchableTime(): String {
    return (DateTime.now()
        .add(0, queryOffsetInSeconds * 1000.0))
        .toString(apiDateTimeFormat) + "+00:00"
}