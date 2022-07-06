package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.coroutines.CoroutineContext

class ApplicationPresenter : ApplicationContract.Presenter() {
    private val stations = listOf(
        "BON",
        "KGX",
        "EUS",
        "MAN",
        "EDB"
    )

    private val stationSubmitButtonText = "View live departures"
    private val stationSubmitButtonLoadingText = "Searching"

    private val baseUrl = "https://mobile-api-softwire2.lner.co.uk/v1/"
    private val buyTicketsBaseUrl = "https://www.lner.co.uk/buy-tickets/booking-engine/"

    private val dispatchers = AppDispatchersImpl()
    private var view: ApplicationContract.View? = null
    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict)
        }
    }

    override fun onViewTaken(view: ApplicationContract.View) {
        this.view = view
        view.populateOriginAndDestinationSpinners(stations)
        view.setStationSubmitButtonText(stationSubmitButtonText)
    }

    override fun onStationSubmitButtonPressed(originStation: String, destinationStation: String) {
        view?.setStationSubmitButtonText(stationSubmitButtonLoadingText)
        launch {
            queryApiForJourneys(originStation, destinationStation)?.let {
                view?.setDepartureTable(extractDepartureInfo(it))
            }
            view?.setStationSubmitButtonText(stationSubmitButtonText)
        }
    }

    private fun extractDepartureInfo(departureDetails: DepartureDetails): List<DepartureInformation> =
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
        } //api

    private fun convertToPriceString(priceInPennies: Int?) = priceInPennies?.let {
        "from £${it / 100}.${padEnd("${it % 100}", "0", 2)}"
    } ?: "sold out" // tool api

    private fun padEnd(numberString: String, padCharacter: String, desiredLength: Int): String {
        var paddedString = numberString
        while (paddedString.length < desiredLength) {
            paddedString += padCharacter
        }
        return paddedString
    } // tool api

    fun generateBuyTicketUrl( //api
        originStation: String,
        destinationStation: String,
        departureDateTime: DateTime
    ) =
        "${buyTicketsBaseUrl}?ocrs=${originStation}&dcrs=${destinationStation}" +
                "&outm=${departureDateTime.month1}&outd=${departureDateTime.dayOfMonth}" +
                "&outh=${departureDateTime.hours}&outmi=${departureDateTime.minutes}&ret=n"


    private suspend fun queryApiForJourneys(  // api
        originStation: String,
        destinationStation: String,
        noChanges: String = "false",
        numberOfAdults: String = "1",
        numberOfChildren: String = "0",
        journeyType: String = "single",
        outboundIsArriveBy: String = "false"
    ): DepartureDetails? {
        try {
            val url = URLBuilder("${baseUrl}fares?")
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
            val departures = client.get<DepartureDetails> { url(url) }
            if (departures.outboundJourneys.isEmpty()) throw NoDeparturesException()

            return departures
        } catch (e: ClientRequestException) {
            val json = Json(JsonConfiguration.Stable)
            val description = json.parse(ErrorResponse.serializer(), e.response.readText())
            view?.presentAlert("Error 💢", description.error_description)
            return null
        } catch (e: NoDeparturesException) {
            view?.presentAlert("🙅 🚂", "No trains found for this route.")
            return null
        }
    }
}

class NoDeparturesException() : Exception("There are no departures") //api