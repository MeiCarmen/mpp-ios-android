package com.jetbrains.handson.mpp.mobile

import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

class ApplicationPresenter : ApplicationContract.Presenter() {
    private val stations = listOf(
        "BON",
        "KGX",
        "EUS",
        "MAN",
        "EDB"
    )

    private var queryId = 0
    private val mutex = Mutex()

    private val stationSubmitButtonText = "View live departures"
    private val stationSubmitButtonLoadingText = "Searching"

    private val dispatchers = AppDispatchersImpl()
    private var view: ApplicationContract.View? = null
    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job


    override fun onViewTaken(view: ApplicationContract.View) {
        this.view = view
        view.populateOriginAndDestinationSpinners(stations)
        view.setStationSubmitButtonText(stationSubmitButtonText)
    }

    override fun onStationSubmitButtonPressed(originStation: String, destinationStation: String) {
        launch {
            var queryUrl = buildQuery(originStation, destinationStation)
            val departureTable = mutableListOf<DepartureInformation>()
            var queryOutput: DepartureDetails?

            try {
                view?.setStationSubmitButtonText(stationSubmitButtonLoadingText)

                val currentQueryId = getNewQueryId()
                view?.setDepartureTable(departureTable) // clear the table
                do {
                    queryOutput = queryApiForJourneys(queryUrl)
                    view?.setStationSubmitButtonText(stationSubmitButtonText)
                    if (queryOutput == null) return@launch

                    appendToDepartureTable(departureTable, queryOutput!!, currentQueryId)
                    queryUrl = Url(baseUrl + "fares" + queryOutput!!.nextOutboundQuery)
                } while (queryOutput!!.nextOutboundQuery != null)

            } catch (e: AlertException) {
                if (departureTable.isEmpty()) view?.presentAlert(e.title, e.description)
            } catch (e: Exception) {
                view?.presentAlert("Something went wrong 😱", e.toString())
            } finally {
                view?.setStationSubmitButtonText(stationSubmitButtonText)
            }
        }
    }

    private suspend fun getNewQueryId(): Int {
        mutex.withLock {
            queryId += 1
            return queryId
        }
    }

    private suspend fun appendToDepartureTable(
        departureTable: MutableList<DepartureInformation>,
        newDepartureInfo: DepartureDetails,
        currentQueryId: Int
    ) {
        departureTable += extractDepartureInfo(newDepartureInfo)
        mutex.withLock {
            if (queryId == currentQueryId) {
                view?.setDepartureTable(departureTable)
            }
        }
    }
}

