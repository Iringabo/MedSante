package com.iringabo.traitement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iringabo.traitement.model.Treatment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TreatmentsAdapter(
    private val items: List<Treatment>,
    private val onView: (Treatment) -> Unit,
    private val onEdit: (Treatment) -> Unit,
    private val onDelete: (Treatment) -> Unit
) : RecyclerView.Adapter<TreatmentsAdapter.VH>() {

    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE
    private val today = LocalDate.now()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treatment, parent, false)
        return VH(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val tr = items[position]
        holder.medName.text = tr.medName
        holder.dates.text = "${tr.startDate} → ${tr.endDate}"

        // Calculer le statut
        val start = LocalDate.parse(tr.startDate, fmt)
        val end   = LocalDate.parse(tr.endDate, fmt)
        val status = when {
            today.isBefore(start)      -> "À venir"
            today.isAfter(end)         -> "Terminé"
            else                        -> "En cours"
        }
        holder.status.text = status
        holder.btnView.setOnClickListener  { onView(tr) }
        holder.btnEdit.setOnClickListener  { onEdit(tr) }
        holder.btnDelete.setOnClickListener{ onDelete(tr) }
    }

    override fun getItemCount() = items.size

    inner class VH(v: View): RecyclerView.ViewHolder(v) {
        val medName   = v.findViewById<TextView>(R.id.tv_med_name)!!
        val dates     = v.findViewById<TextView>(R.id.tv_dates)!!
        val status    = v.findViewById<TextView>(R.id.tv_status)!!
        val btnView   = v.findViewById<ImageButton>(R.id.btn_view)!!
        val btnEdit   = v.findViewById<ImageButton>(R.id.btn_edit)!!
        val btnDelete = v.findViewById<ImageButton>(R.id.btn_delete)!!
    }
}