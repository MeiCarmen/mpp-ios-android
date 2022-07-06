package com.jetbrains.handson.mpp.mobile

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String, val error_description: String)

@Serializable
data class DepartureDetails(
    val outboundJourneys: List<JourneyDetails>
)

@Serializable
data class JourneyDetails(
    val originStation: Station,
    val destinationStation: Station,
    val departureTime: String,
    val arrivalTime: String,
    val primaryTrainOperator: TrainOperatorDetails,
    val tickets: List<TicketDetails>,
    val journeyDurationInMinutes: Int
)

@Serializable
data class Station(
    val crs: String
)

@Serializable
data class TrainOperatorDetails(
    val name: String
)

@Serializable
data class TicketDetails(
    val priceInPennies: Int
)

data class DepartureInformation(
    val departureTime: String,
    val arrivalTime: String,
    val journeyTime: String,
    val trainOperator: String,
    val price: String,
    val buyUrl: String
)