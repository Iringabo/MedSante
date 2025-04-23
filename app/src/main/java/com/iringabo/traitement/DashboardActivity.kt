@file:Suppress("KotlinConstantConditions")

package com.iringabo.traitement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.adapter.NextPillsAdapter
import com.iringabo.traitement.model.Treatment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DashboardActivity : ComponentActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { FirebaseFirestore.getInstance() }

    private lateinit var rvNextPills: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabViewTreatments: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        rvNextPills = findViewById(R.id.rv_next_pills)
        fabAdd      = findViewById(R.id.fab_add_treatment)
        rvNextPills.layoutManager = LinearLayoutManager(this)

        fabViewTreatments = findViewById(R.id.fab_view_treatments)
        fabViewTreatments.setOnClickListener {
            startActivity(Intent(this, TreatmentsActivity::class.java))
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, TreatManActivity::class.java))
        }

        loadDashboard()
    }

    private fun loadDashboard() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("treatments").document(uid)
            .collection("userTreatments")
            .get()
            .addOnSuccessListener { snap ->
                val today = LocalDate.now()

                val treatments = snap.documents
                    .mapNotNull { it.toObject(Treatment::class.java) }

                displayNextPills(treatments, today)
                displayHistory(treatments, today)
                displayObservance(treatments, today)
            }
    }

    private fun displayNextPills(list: List<Treatment>, today: LocalDate) {
        val now = LocalTime.now()
        val timeFmt = DateTimeFormatter.ofPattern("HH:mm")
        val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE

        val upcoming: List<Pair<String, String>> = list.flatMap { t ->
            // parse treatment start/end dates
            val start = LocalDate.parse(t.startDate, dateFmt)
            val end   = LocalDate.parse(t.endDate,   dateFmt)

            // assume t.times is a comma‑separated string like "08:00,12:00,18:00"
            t.times.mapNotNull { timeStr ->
                try {
                    val time = LocalTime.parse(timeStr, timeFmt)
                    if (start <= today && end >= today && time.isAfter(now)) {
                        timeStr to t.medName
                    } else null
                } catch (e: DateTimeParseException) {
                    Log.e(
                        "DashboardActivity",
                        "Invalid time format: $timeStr in treatment ${t.medName}"
                    )
                    null
                }
            }
        }
            .sortedBy { it.first }   // String.compareTo is operator fun

        rvNextPills.adapter = NextPillsAdapter(upcoming)
    }

    private fun displayHistory(list: List<Treatment>, today: LocalDate) {
        // TODO: replace this with your real log count
        val historyCount = 0

        // count how many doses *should* have been taken today
        val scheduled = list
            .filter { LocalDate.parse(it.startDate) <= today &&
                    LocalDate.parse(it.endDate)   >= today }
            .sumOf { t ->
                // split the same way we did above
                t.times.size
            }

        val tvHistory = findViewById<TextView>(R.id.tv_history_content)
        tvHistory.text = if (historyCount > 0) {
            "$historyCount prises sur $scheduled prévues"
        } else {
            "Aucun historique pour aujourd'hui"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayObservance(list: List<Treatment>, today: LocalDate) {
        // total scheduled doses across *all* treatments (not just today)
        val scheduledAll = list.sumOf { t ->
            t.times.size
        }
        val takenAll     = 0  // your real data source here

        val entries = listOf(
            PieEntry(takenAll.toFloat(), "Pris"),
            PieEntry((scheduledAll - takenAll).toFloat(), "Manqué")
        )
        val set   = PieDataSet(entries, "Observance")
        val data  = PieData(set)

        val chart = findViewById<com.github.mikephil.charting.charts.PieChart>(
            R.id.piechart_observance
        )
        chart.data = data
        chart.invalidate()

        val tvPercent = findViewById<TextView>(R.id.tv_observance_percent)
        val percent   = if (scheduledAll > 0) takenAll * 100 / scheduledAll else 0
        tvPercent.text = "$percent%"
    }
}
