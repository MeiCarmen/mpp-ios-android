package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.parse

const val queryOffsetInSeconds = 6 * 60
const val apiDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

fun apiTimeToDateTime(apiTime: String) = DateFormat(apiDateTimeFormat).parse(apiTime).local

fun extractSimpleTime(time: String) = apiTimeToDateTime(time).toString("HH:mm")

fun convertToHoursAndMinutes(minutes: Int) = "${minutes / 60}h ${minutes % 60}min"

fun getEarliestSearchableTime() =
    DateTime.now().add(0, queryOffsetInSeconds * 1000.0).local.toString(apiDateTimeFormat)