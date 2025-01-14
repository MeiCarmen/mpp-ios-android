package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.CoroutineScope

interface ApplicationContract {
    interface View {
        fun populateOriginAndDestinationSpinners(stations: List<String>)
        fun setStationSubmitButtonText(text: String)
        fun setDepartureTable(departures: List<DepartureInformation>)
        fun presentAlert(title: String, message: String)
    }

    abstract class Presenter : CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun onStationSubmitButtonPressed(originStation: String, destinationStation: String)
    }
}
