package com.jetbrains.handson.mpp.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.departure_cell.view.*

class RecyclerAdapter(private val openUrl: (url: String) -> Unit) : RecyclerView.Adapter<RecyclerAdapter.DepartureCellHolder>() {
    private var departures = emptyList<DepartureInformation>()

    fun setDepartures(departures: List<DepartureInformation>) {
        this.departures = departures
        notifyDataSetChanged()
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
        holder.update(departures[position], openUrl)
    }

    override fun getItemCount() = departures.size

    class DepartureCellHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun update(departure: DepartureInformation, openUrl: (url: String) -> Unit) {
            view.departure_time.text = departure.departureTime
            view.arrival_time.text = departure.arrivalTime
            view.price.text = departure.price
            view.travel_time.text = departure.journeyTime
            view.operator.text = departure.trainOperator
            view.purchase.text = "Buy"

            view.purchase.setOnClickListener { openUrl(departure.buyUrl) }
        }
    }

}




