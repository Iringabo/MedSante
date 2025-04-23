package com.iringabo.traitement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.adapter.TodayTreatmentsAdapter
import com.iringabo.traitement.model.Treatment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class TodayTreatmentsActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private lateinit var adapter: TodayTreatmentsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_treatments)

        setupRecyclerView()
        loadTodayTreatments()

    }

    private fun setupRecyclerView() {
        adapter = TodayTreatmentsAdapter(emptyList())
        findViewById<RecyclerView>(R.id.rv_today_treatments).apply {
            layoutManager = LinearLayoutManager(this@TodayTreatmentsActivity)
            adapter = this@TodayTreatmentsActivity.adapter
        }
    }

    private fun loadTodayTreatments() {
        val uid = auth.currentUser?.uid ?: return
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        db.collection("treatments")
            .document(uid)
            .collection("userTreatments")
            .get()
            .addOnSuccessListener { snap ->
                val treatments = snap.documents.mapNotNull { doc ->
                    doc.toObject(Treatment::class.java)?.apply { id = doc.id }
                }

                val todayTreatments = treatments.filter { treatment ->
                    val startDate = LocalDate.parse(treatment.startDate, dateFormatter)
                    val endDate = LocalDate.parse(treatment.endDate, dateFormatter)
                    !today.isBefore(startDate) && !today.isAfter(endDate)
                }.map { treatment ->
                    Pair(treatment.medName, treatment.times)
                }

                adapter = TodayTreatmentsAdapter(todayTreatments)
                findViewById<RecyclerView>(R.id.rv_today_treatments).adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
        }
}