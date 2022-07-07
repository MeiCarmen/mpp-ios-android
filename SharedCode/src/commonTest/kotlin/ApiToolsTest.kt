package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class ApiToolsTests {
    @Test
    fun extractDepartureInformation_ReturnsEmptyList_WhenNoOutboundJourneys() {
        val input = DepartureDetails(emptyList(), "")
        val output = extractDepartureInfo(input)
        assertTrue(output.isEmpty())
    }

    @Test
    fun extractDepartureInformation_ValidExample() {
        val input = DepartureDetails(
            listOf(
                JourneyDetails(
                    Station("origin"),
                    Station("destination"),
                    "2000-04-15T11:14:00.000Z",
                    "2000-04-15T12:15:00.000Z",
                    TrainOperatorDetails("LNER"),
                    listOf(TicketDetails(1000), TicketDetails(2000)),
                    61
                )
            ), ""
        )
        val expectedOutput = listOf(
            DepartureInformation(
                "11:14",
                "12:15",
                "1h 1min",
                "LNER",
                "from £10.00",
                generateBuyTicketUrl(
                    "origin",
                    "destination",
                    apiTimeToDateTime("2000-04-15T11:14:00.000Z")
                )
            )
        )
        assertEquals(expectedOutput, extractDepartureInfo(input))
    }

    @Test
    fun generateBuyTicketUrl_SameResults_WhenStationsEmpty() {
        val inputDateTime = DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2000-04-15T11:14:00.000Z").local
        val expectedOutput = "https://www.lner.co.uk/buy-tickets/booking-engine/?ocrs=&dcrs=&outm=4&outd=15&outh=11&outmi=14&ret=n"
        assertEquals(generateBuyTicketUrl("","",inputDateTime), expectedOutput)
    }

    @Test
    fun generateBuyTicketUrl_ValidExample() {
        val inputDateTime = DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2000-04-15T11:14:00.000Z").local
        val expectedOutput = "https://www.lner.co.uk/buy-tickets/booking-engine/?ocrs=BON&dcrs=MAN&outm=4&outd=15&outh=11&outmi=14&ret=n"
        assertEquals(generateBuyTicketUrl("BON","MAN",inputDateTime), expectedOutput)
    }

    // padEnd
    @Test
    fun padEnd_ThrowsIllegalArgumentException_WhenDesiredLengthLessThanNumberStringLength() {
        assertFailsWith<IllegalArgumentException> { padEnd("10", '0', 1) }
    }
    // working
    @Test
    fun padEnd_ValidExample() {
        assertEquals("7.00", padEnd("7.0", '0', 4))
    }

    // convertToPriceString
    // input null
    //input neg
    // working example
    // example with no pennies aka check that padded


    // buildQuery


}