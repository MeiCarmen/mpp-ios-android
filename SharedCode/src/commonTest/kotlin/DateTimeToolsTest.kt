package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateException
import com.soywiz.klock.DateTime
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DateTimeToolsTests {
    @Test
    fun apiTimeToDateTime_ThrowsDateException_WhenPassedEmptyString() {
        assertFailsWith<DateException> { apiTimeToDateTime("") }
    }

    @Test
    fun apiTimeToDateTime_ThrowsDateException_WhenPassedNonTimeString() {
        assertFailsWith<DateException> { apiTimeToDateTime("this is not a time string") }
    }

    @Test
    fun apiTimeToDateTime_ThrowsDateException_WhenPassedTimeWithoutTimeZone() {
        assertFailsWith<DateException> { apiTimeToDateTime("2000-04-15T11:14:00.000") }
    }

    @Test
    fun apiTimeToDateTime_Works_WhenPassedTimeWithNegativeTimeZone() {
        assertEquals(DateTime.invoke(2000, 4, 15, 11, 14, 0, 0), apiTimeToDateTime("2000-04-15T11:14:00.000-01:00"))
    }

    @Test
    fun apiTimeToDateTime_Works_WhenPassedTimeWithPositiveTimeZone() {
        assertEquals(DateTime.invoke(2000, 4, 15, 11, 14, 0, 0), apiTimeToDateTime("2000-04-15T11:14:00.000+02:00"))
    }

    @Test
    fun apiTimeToDateTime_Works_WhenPassedTimeEndingInZ() {
        assertEquals(DateTime.invoke(2000, 4, 15, 11, 14, 0, 0), apiTimeToDateTime("2000-04-15T11:14:00.000Z"))
    }

    @Test
    fun extractSimpleTime_ThrowsDateException_WhenPassedEmptyString() {
        assertFailsWith<DateException> { extractSimpleTime("") }
    }

    @Test
    fun extractSimpleTime_ThrowsDateException_WhenPassedNonTimeString() {
        assertFailsWith<DateException> { extractSimpleTime("this is not a time string") }
    }

    @Test
    fun extractSimpleTime_ThrowsDateException_WhenPassedTimeWithoutTimeZone() {
        assertFailsWith<DateException> { extractSimpleTime("2000-04-15T11:14:00.000") }
    }

    @Test
    fun extractSimpleTime_Works_WhenPassedTimeWithNegativeTimeZone() {
        assertEquals("11:14", extractSimpleTime("2000-04-15T11:14:00.000-01:00"))
    }

    @Test
    fun extractSimpleTime_Works_WhenPassedTimeWithPositiveTimeZone() {
        assertEquals("11:14", extractSimpleTime("2000-04-15T11:14:00.000+02:00"))
    }

    @Test
    fun extractSimpleTime_Works_WhenPassedTimeEndingInZ() {
        assertEquals("11:14", extractSimpleTime("2000-04-15T11:14:00.000Z"))
    }

    @Test
    fun convertToHoursAndMinutes_ThrowsIllegalArgumentException_WhenPassedNegativeMinutes() {
        assertFailsWith<IllegalArgumentException> { convertToHoursAndMinutes(-1) }
    }

    @Test
    fun covertToHoursAndMinutes_Works_WhenPassedPositiveMinutes() {
        assertEquals("1h 9min", convertToHoursAndMinutes(69))
    }

    @Test
    fun convertToHoursAndMinutes_Works_WhenPassedZeroMinutes() {
        assertEquals("0h 0min", convertToHoursAndMinutes(0))
    }

    @Test
    fun getEarliestSearchableTime_ReturnsTimeWithinOneHunderedMillisecondsOfSixMinutesInTheFuture() {
        // open to suggestions on how to write this better
        val expectedEarliestTime = DateTime.now().add(0, 6 * 60 * 1000.0).local.local
        val actualEarliestTime = apiTimeToDateTime(getEarliestSearchableTime())

        // Compare to a precision of 100 milliseconds
        assertTrue( (actualEarliestTime.unixMillisLong - expectedEarliestTime.unixMillisLong).absoluteValue < 100 )
    }
}