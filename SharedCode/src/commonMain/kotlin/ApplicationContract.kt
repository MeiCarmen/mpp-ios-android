package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.CoroutineScope

interface ApplicationContract {
    interface View {
        fun populateOriginAndDestinationSpinners(stations: List<String>)
        fun setStationSubmitButtonHandler()
        fun setStationSubmitButtonText(text: String)
        fun getOriginStation() : String
        fun getDestinationStation() : String
        fun openUrl(url: String)
    }

    abstract class Presenter: CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun onStationSubmitButtonPressed()
    }
}
