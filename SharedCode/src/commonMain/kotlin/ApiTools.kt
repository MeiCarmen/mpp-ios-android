package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateTime
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.coroutines.flow.flow

const val baseUrl = "https://mobile-api-softwire2.lner.co.uk/v1/"
const val buyTicketsBaseUrl = "https://www.lner.co.uk/buy-tickets/booking-engine/"


val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json.nonstrict)
    }
}

class AlertException(val title: String, val description: String) : Exception()

fun journeys(originStation: String, destinationStation: String) = flow {
    var queryUrl: Url? = buildQuery(originStation, destinationStation)
    do {
        val (departureInfos, nextQueryUrl) = getDeparturesAndNextQuery(queryUrl!!)
        departureInfos.forEach { emit(it) }
        queryUrl = nextQueryUrl
    } while (queryUrl != null)
}

suspend fun getDeparturesAndNextQuery(url: Url): Pair<List<DepartureInformation>, Url?> {
    val queryOutput = queryApiForJourneys(url)
    return Pair(
        extractDepartureInfo(queryOutput),
        queryOutput.nextOutboundQuery?.let { Url(baseUrl + "fares" + it) })
}

suspend fun queryApiForJourneys(
    url: Url
): DepartureDetails {
    try {
        val departures = client.get<DepartureDetails> { url(url) }
        if (departures.outboundJourneys.isEmpty()) throw AlertException(
            "🙅 🚂",
            "No trains found for this route."
        )
        return departures
    } catch (e: ClientRequestException) {
        val json = Json(JsonConfiguration.Stable)
        val description = json.parse(ErrorResponse.serializer(), e.response.readText())
        throw AlertException("Error 💢", description.error_description)
    }
}


fun extractDepartureInfo(departureDetails: DepartureDetails): List<DepartureInformation> =
    departureDetails.outboundJourneys.map {
        DepartureInformation(
            extractSimpleTime(it.departureTime),
            extractSimpleTime(it.arrivalTime),
            convertToHoursAndMinutes(it.journeyDurationInMinutes),
            it.primaryTrainOperator.name,
            convertToPriceString(it.tickets.map { ticket -> ticket.priceInPennies }.min()),
            generateBuyTicketUrl(
                it.originStation.crs,
                it.destinationStation.crs,
                apiTimeToDateTime(it.departureTime)
            )
        )
    }

fun generateBuyTicketUrl(
    originStation: String,
    destinationStation: String,
    departureDateTime: DateTime
) = "${buyTicketsBaseUrl}?ocrs=${originStation}&dcrs=${destinationStation}" +
        "&outm=${departureDateTime.month1}&outd=${departureDateTime.dayOfMonth}" +
        "&outh=${departureDateTime.hours}&outmi=${departureDateTime.minutes}&ret=n"


fun padEnd(numberString: String, padCharacter: Char, desiredLength: Int): String {
    if (numberString.length>desiredLength) throw IllegalArgumentException()
    var paddedString = numberString
    while (paddedString.length < desiredLength) {
        paddedString += padCharacter
    }
    return paddedString
}

fun convertToPriceString(priceInPennies: Int?) = priceInPennies?.let {
    "from £${it / 100}.${padEnd("${it % 100}", '0', 2)}"
} ?: "sold out"

fun buildQuery(
    originStation: String,
    destinationStation: String,
    noChanges: String = "false",
    numberOfAdults: String = "1",
    numberOfChildren: String = "0",
    journeyType: String = "single",
    outboundIsArriveBy: String = "false"
) = URLBuilder("${baseUrl}fares?")
    .apply {
        parameters["originStation"] = originStation
        parameters["destinationStation"] = destinationStation
        parameters["noChanges"] = noChanges
        parameters["numberOfAdults"] = numberOfAdults
        parameters["numberOfChildren"] = numberOfChildren
        parameters["journeyType"] = journeyType
        parameters["outboundDateTime"] = getEarliestSearchableTime()
        parameters["outboundIsArriveBy"] = outboundIsArriveBy
    }
    .build()
