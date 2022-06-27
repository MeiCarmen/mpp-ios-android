package com.jetbrains.handson.mpp.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val presenter = ApplicationPresenter()
        presenter.onViewTaken(this)
    }

    override fun setLabel(text: String) {
        findViewById<TextView>(R.id.main_text).text = text
    }

    override fun populateDepartureAndArrivalSpinners(stations: Array<String>) {
        val departureSpinner: Spinner = findViewById(R.id.departure_station_spinner)
        val departureAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stations)
        departureSpinner.adapter = departureAdapter

        val arrivalSpinner: Spinner = findViewById(R.id.departure_station_spinner)
        val arrivalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stations)
        arrivalSpinner.adapter = arrivalAdapter
    }
}
