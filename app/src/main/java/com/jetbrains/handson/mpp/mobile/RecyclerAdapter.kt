package com.jetbrains.handson.mpp.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.DepartureCellHolder>() {
    private var departures = emptyList<DepartureInformation>()

    fun setDepartures(departures: List<DepartureInformation>) {
        this.departures = departures
        println(this.departures)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DepartureCellHolder {
        val cell =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.departure_cell, parent, false)
        return DepartureCellHolder(cell)
    }

    override fun onBindViewHolder(holder: DepartureCellHolder, position: Int) {
        holder.update(departures[position])
    }

    override fun getItemCount() = departures.size

    class DepartureCellHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        fun update(departure: DepartureInformation) {
            v.findViewById<TextView>(R.id.departure_time).text = departure.departureTime
            v.findViewById<TextView>(R.id.arrival_time).text = departure.arrivalTime
            v.findViewById<TextView>(R.id.price).text = departure.price
            v.findViewById<TextView>(R.id.travel_time).text = departure.journeyTime
            v.findViewById<TextView>(R.id.operator).text = departure.trainOperator
            v.findViewById<TextView>(R.id.purchase).text = "Buy"
        }
    }

}




