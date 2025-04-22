package com.iringabo.traitement.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iringabo.traitement.databinding.ItemReminderBinding
import com.iringabo.traitement.model.Treatment

class ReminderAdapter(
    private val items: MutableList<Treatment>,
    private val onChoice: (Treatment, String) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.VH>() {

    inner class VH(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        holder.binding.tvMedName.text = t.medName
        holder.binding.tvTime.text = t.times.find { true } // only due ones

        holder.binding.btnTaken.setOnClickListener {
            onChoice(t, "Pris")
        }
        holder.binding.btnPostpone.setOnClickListener {
            onChoice(t, "Reporter")
        }
        holder.binding.btnIgnore.setOnClickListener {
            onChoice(t, "Ignorer")
        }
    }

    override fun getItemCount() = items.size

    fun removeItem(t: Treatment) {
        val idx = items.indexOf(t)
        if (idx >= 0) {
            items.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }
}
