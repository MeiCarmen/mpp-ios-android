package com.jetbrains.handson.mpp.mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ApplicationContract.View {
    private lateinit var presenter: ApplicationPresenter
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = ApplicationPresenter()
        presenter.onViewTaken(this)

        setupSubmitButton()
        setupRecyclerView()
    }

    private fun setupSubmitButton() {
        station_submit_button.setOnClickListener {
            submitButtonHandler()
        }
    }

    private fun submitButtonHandler() {
        val originStation = origin_station_spinner.selectedItem.toString()
        val destinationStation = destination_station_spinner.selectedItem.toString()
        presenter.onStationSubmitButtonPressed(originStation, destinationStation)
    }

    private fun setupRecyclerView() {
        recyclerAdapter = RecyclerAdapter(::openUrl)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )

    }

    override fun setStationSubmitButtonText(text: String) {
        station_submit_button.text = text
    }

    override fun populateOriginAndDestinationSpinners(stations: List<String>) {
        populateSpinner(origin_station_spinner, stations)
        populateSpinner(destination_station_spinner, stations)
    }

    private fun populateSpinner(spinner: Spinner, content: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, content)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
    }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun setDepartureTable(departures: List<DepartureInformation>) {
        recyclerAdapter.setDepartures(departures)
    }

}
