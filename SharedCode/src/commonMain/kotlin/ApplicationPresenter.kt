package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

class ApplicationPresenter : ApplicationContract.Presenter() {
    private val stations = listOf(
        "BON", "KGX", "EUS", "MAN", "EDB"
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
            val currentQueryId = getNewQueryId()
            view?.setStationSubmitButtonText(stationSubmitButtonLoadingText)
            view?.clearDepartureTable()
            var firstQuery = true
            try {
                journeys(originStation, destinationStation)
                    .collect {
                        appendToDepartureTable(it, currentQueryId)
                        view?.setStationSubmitButtonText(stationSubmitButtonText)
                        firstQuery = false
                    }
            } catch (e: AlertException) {
                if (firstQuery) view?.presentAlert(e.title, e.description)
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
        newDepartureInfo: DepartureInformation,
        currentQueryId: Int
    ) {
        mutex.withLock {
            if (queryId == currentQueryId) {
                view?.appendToDepartureTable(newDepartureInfo)
            }
        }
    }
}

