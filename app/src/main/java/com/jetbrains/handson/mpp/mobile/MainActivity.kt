package com.jetbrains.handson.mpp.mobile

import android.content.Intent
import android.net.Uri
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

    override fun populateOriginAndDestinationSpinners(stations: List<String>) {
        // TODO refactor pretty
        populateSpinner(R.id.origin_station_spinner, stations)
        populateSpinner(R.id.destination_station_spinner, stations)
    }

    fun populateSpinner(id: Int, content: List<String>){
        val spinner: Spinner = findViewById(id)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, content)
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.adapter = adapter
    }

    override fun getOriginStation(): String {
        val departureSpinner: Spinner = findViewById(R.id.origin_station_spinner)
        return departureSpinner.selectedItem.toString()
    }

    override fun getDestinationStation(): String {
        val arrivalSpinner: Spinner = findViewById(R.id.destination_station_spinner)
        return arrivalSpinner.selectedItem.toString()
    }

    override fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

}
