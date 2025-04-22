package com.iringabo.traitement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NextPillsAdapter(
    private val items: List<Pair<String, String>>
) : RecyclerView.Adapter<NextPillsAdapter.PillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        // Using Android's simple two-line list item layout
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return PillViewHolder(view)
    }

    override fun onBindViewHolder(holder: PillViewHolder, position: Int) {
        val (time, medName) = items[position]
        holder.timeText.text = time
        holder.medNameText.text = medName
    }

    override fun getItemCount(): Int = items.size

    class PillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeText: TextView = itemView.findViewById(android.R.id.text1)
        val medNameText: TextView = itemView.findViewById(android.R.id.text2)
    }
}
