package com.jetbrains.handson.mpp.mobile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    private lateinit var presenter: ApplicationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = ApplicationPresenter()

        presenter.onViewTaken(this)

        findViewById<Button>(R.id.station_submit_button).setOnClickListener {
            submitButtonHandler()
        }
    }

    private fun submitButtonHandler() {
        val originStation =
            findViewById<Spinner>(R.id.origin_station_spinner).selectedItem.toString()
        val destinationStation =
            findViewById<Spinner>(R.id.destination_station_spinner).selectedItem.toString()
        presenter.onStationSubmitButtonPressed(originStation, destinationStation)
    }


    override fun setStationSubmitButtonText(text: String) {
        findViewById<Button>(R.id.station_submit_button).text = text
    }

    override fun populateOriginAndDestinationSpinners(stations: List<String>) {
        populateSpinner(R.id.origin_station_spinner, stations)
        populateSpinner(R.id.destination_station_spinner, stations)
    }

    private fun populateSpinner(id: Int, content: List<String>) {
        val spinner: Spinner = findViewById(id)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, content)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
    }

    override fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun setDepartureTable(departures: List<DepartureInformation>) {
        //todo
        return
    }

}
