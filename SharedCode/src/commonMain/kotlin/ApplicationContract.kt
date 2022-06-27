package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.CoroutineScope

interface ApplicationContract {
    interface View {
        fun setLabel(text: String)
        fun populateDepartureAndArrivalSpinners(stations: Array<String>)
        fun setStationSubmitButtonHandler()
        fun setStationSubmitButtonText(text: String)
    }

    abstract class Presenter: CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun onStationSubmitButtonPressed()
    }
}
