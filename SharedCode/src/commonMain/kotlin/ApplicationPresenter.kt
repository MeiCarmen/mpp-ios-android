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
            try {
                view?.setStationSubmitButtonText(stationSubmitButtonLoadingText)
                queryApiForJourneys(originStation, destinationStation).let {
                    view?.setDepartureTable(extractDepartureInfo(it))
                }
                view?.setStationSubmitButtonText(stationSubmitButtonText)
            } catch (e: AlertException) {
                view?.presentAlert(e.title, e.description)
            } catch (e: Exception) {
                view?.presentAlert("Something went wrong 😱", e.toString())
            } finally {
                view?.setStationSubmitButtonText(stationSubmitButtonText)
            }
        }
    }
}