package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.parse
import io.ktor.http.*
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
        val inputDateTime =
            DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2000-04-15T11:14:00.000Z").local
        val expectedOutput =
            "https://www.lner.co.uk/buy-tickets/booking-engine/?ocrs=&dcrs=&outm=4&outd=15&outh=11&outmi=14&ret=n"
        assertEquals(generateBuyTicketUrl("", "", inputDateTime), expectedOutput)
    }

    @Test
    fun generateBuyTicketUrl_ValidExample() {
        val inputDateTime =
            DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2000-04-15T11:14:00.000Z").local
        val expectedOutput =
            "https://www.lner.co.uk/buy-tickets/booking-engine/?ocrs=BON&dcrs=MAN&outm=4&outd=15&outh=11&outmi=14&ret=n"
        assertEquals(generateBuyTicketUrl("BON", "MAN", inputDateTime), expectedOutput)
    }

    @Test
    fun padEnd_ThrowsIllegalArgumentException_WhenDesiredLengthLessThanNumberStringLength() {
        assertFailsWith<IllegalArgumentException> { padEnd("10", '0', 1) }
    }

    @Test
    fun padEnd_ValidExample() {
        assertEquals("7.00", padEnd("7.0", '0', 4))
    }

    @Test
    fun extractPriceString_ReturnsSoldOut_WhenInputEmpty() {
        assertEquals("sold out", extractPriceString(emptyList()))
    }

    @Test
    fun extractPriceString_ThrowsIllegalArgumentException_WhenArgumentNegative() {
        assertFailsWith<IllegalArgumentException> {
            extractPriceString(
                listOf(
                    TicketDetails(-234),
                    TicketDetails(234)
                )
            )
        }
    }

    @Test
    fun extractPriceString_ExtractsTheLowestPrice() {
        assertEquals(
            "from £1.11",
            extractPriceString(listOf(TicketDetails(333), TicketDetails(111), TicketDetails(1111)))
        )
    }

    @Test
    fun extractPriceString_PenniesPadded() {
        assertEquals(
            "from £1.00",
            extractPriceString(listOf(TicketDetails(333), TicketDetails(100), TicketDetails(1111)))
        )
    }

    @Test
    fun buildQuery_UsesDefaultValues() {
        val expectedOutput =
            "https://mobile-api-softwire2.lner.co.uk/v1/fares?originStation=EDB&destinationStation=KGX&noChanges=false&numberOfAdults=1&numberOfChildren=0&journeyType=single&outboundDateTime=2022-07-24T14%3A30%3A00.000%2B00%3A00&outboundIsArriveBy=false"
        val inputTime = "2022-07-24T14:30:00.000+00:00"
        assertEquals(
            expectedOutput,
            buildQuery("EDB", "KGX", outboundDateTime = inputTime).toString()
        )
    }

    @Test
    fun buildQuery_AccepthOptionalArgs() {
        val expectedOutput =
            "https://mobile-api-softwire2.lner.co.uk/v1/fares?originStation=EDB&destinationStation=KGX&noChanges=true&numberOfAdults=2&numberOfChildren=20&journeyType=return&outboundDateTime=2022-07-24T14%3A30%3A00.000%2B00%3A00&outboundIsArriveBy=false"
        val inputTime = "2022-07-24T14:30:00.000+00:00"
        assertEquals(
            expectedOutput,
            buildQuery("EDB", "KGX", "true", "2", "20", "return", inputTime, "false").toString()
        )
    }
}