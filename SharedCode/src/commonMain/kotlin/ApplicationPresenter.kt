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

    private val stationSubmitButtonText = "View live departures"
    private val stationSubmitButtonLoadingText = "Searching"

    private val noChangesDefault = "false"
    private val numberOfAdultsDefault = "1"
    private val numberOfChildrenDefault = "0"
    private val journeyTypeDefault = "single"
    private val outboundIsArriveByDefault = "false"

    private val queryOffsetInSeconds = 6*60
    private val apiDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"

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
        view?.setStationSubmitButtonText(stationSubmitButtonLoadingText)
        launch {
            val departureDetails = queryApiForJourneys(originStation, destinationStation)
            view?.setDepartureTable(extractDepartureInfo(departureDetails))
            view?.setStationSubmitButtonText(stationSubmitButtonText)
        }
    }

    fun extractDepartureInfo(departureDetails: DepartureDetails?): List<DepartureInformation> {
        if (departureDetails == null) return emptyList()

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
        val localTimeFormat = DateFormat(apiDateTimeFormat)
        val timeWithoutTimeZone = time.split("+").first()
        val localTime = localTimeFormat.parse(timeWithoutTimeZone)
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
            .add(0, queryOffsetInSeconds*1000.0))
            .toString(apiDateTimeFormat) + "+00:00"
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
