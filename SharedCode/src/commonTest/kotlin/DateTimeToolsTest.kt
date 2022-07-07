package com.jetbrains.handson.mpp.mobile

import kotlin.test.assertEquals
import kotlin.test.Test

class SampleTests {
    @Test
    fun testExample() {
        assertEquals(convertToHoursAndMinutes(100), "1h 40min")
    }
}