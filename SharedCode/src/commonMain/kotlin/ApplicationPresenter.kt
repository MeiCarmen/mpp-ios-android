package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class ApplicationPresenter : ApplicationContract.Presenter() {
    private val stations = listOf(
        "BON",
        "KGX",
        "EUS",
        "MAN",
        "EDB"
    )

    private val stationSubmitButtonText = "Search"

    private val noChangesDefault = "false"
    private val numberOfAdultsDefault = "1"
    private val numberOfChildrenDefault = "0"
    private val journeyTypeDefault = "single"
    private val outboundIsArriveByDefault = "false"

    private val baseUrl = "https://mobile-api-softwire2.lner.co.uk/v1/"

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
        var departureDetails: DepartureDetails? = null
        launch {
            departureDetails = queryApiForJourneys(originStation, destinationStation)
            if (departureDetails == null) {
                view?.setDepartureTable(emptyList())
            } else {
                view?.setDepartureTable(extractDepartureInfo(departureDetails!!))
            }
        }
    }

    fun extractDepartureInfo(departureDetails: DepartureDetails): List<DepartureInformation> {
        return departureDetails.outboundJourneys.map {
            DepartureInformation(
                extractSimpleTime(it.departureTime),
                extractSimpleTime(it.arrivalTime),
                convertToHoursAndMinutes(it.journeyDurationInMinutes),
                it.primaryTrainOperator.name,
                convertToPriceString(it.tickets.map { ticket -> ticket.priceInPennies }.min())
            )
        }
    }

    fun extractSimpleTime(time: String): String {
        val utcTimeFormat: DateFormat = DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val utcTime = utcTimeFormat.parse(time.split("+").first())
        val timezoneOffset = time.split("+").last().split(":")
        val timeZoneHours = timezoneOffset.first().toInt().hours
        val timeZoneMinutes = timezoneOffset.last().toInt().minutes
        val localTime = utcTime + timeZoneHours + timeZoneMinutes
        return localTime.toString("HH:mm")
    }

    fun convertToHoursAndMinutes(journeyDurationInMinutes: Int): String {
        return "${journeyDurationInMinutes / 60}h ${journeyDurationInMinutes % 60}min"
    }

    fun convertToPriceString(priceInPennies: Int?): String {
        if (priceInPennies == null) return "sold out"
        return "£${priceInPennies / 100}.${priceInPennies % 100}"
    }

    fun getImminentDateTime(): String {
        return (DateTime.now()
            .add(0, 1000000.0))
            .toString("yyyy-MM-dd'T'HH:mm:ss.SSS") + "+00:00"
    }

    suspend fun queryApiForJourneys(
        originStation: String,
        destinationStation: String
    ): DepartureDetails? {
        try {
            val url = URLBuilder("${baseUrl}fares?")
                .apply {
                    parameters["originStation"] = originStation
                    parameters["destinationStation"] = destinationStation
                    parameters["noChanges"] = noChangesDefault
                    parameters["numberOfAdults"] = numberOfAdultsDefault
                    parameters["numberOfChildren"] = numberOfChildrenDefault
                    parameters["journeyType"] = journeyTypeDefault
                    parameters["outboundDateTime"] = getImminentDateTime()
                    parameters["outboundIsArriveBy"] = outboundIsArriveByDefault
                }
                .build()
            return client.get<DepartureDetails> { url(url) }
        } catch (e: Exception) {
            println(e.toString())
            return null
        }
    }
}
