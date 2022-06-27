package com.jetbrains.handson.mpp.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    private lateinit var presenter: ApplicationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = ApplicationPresenter()

        presenter.onViewTaken(this)
    }

    override fun setStationSubmitButtonText(text: String) {
        findViewById<Button>(R.id.station_submit_button).text = text
    }

    override fun setStationSubmitButtonHandler() {
        findViewById<Button>(R.id.station_submit_button).setOnClickListener { presenter.onStationSubmitButtonPressed() }
    }

    override fun setLabel(text: String) {
        findViewById<TextView>(R.id.main_text).text = text
    }

    override fun populateDepartureAndArrivalSpinners(stations: Array<String>) {
        val departureSpinner: Spinner = findViewById(R.id.departure_station_spinner)
        val departureAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stations)
        departureAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        departureSpinner.adapter = departureAdapter

        val arrivalSpinner: Spinner = findViewById(R.id.arrival_station_spinner)
        val arrivalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stations)
        arrivalAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        arrivalSpinner.adapter = arrivalAdapter
    }
}
