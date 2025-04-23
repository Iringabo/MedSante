package com.iringabo.traitement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iringabo.traitement.R

class TodayTreatmentsAdapter(
    private val items: List<Pair<String, List<String>>>
) : RecyclerView.Adapter<TodayTreatmentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_treatment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (medName, times) = items[position]
        holder.tvMedName.text = medName
        holder.tvTimes.text = times.joinToString(", ")
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMedName: TextView = itemView.findViewById(R.id.tv_med_name)
        val tvTimes: TextView = itemView.findViewById(R.id.tv_times)
        }
}