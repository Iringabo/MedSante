package com.iringabo.traitement.reminder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iringabo.traitement.databinding.ActivityReminderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.model.Treatment
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val fmt = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reminderToolbar.setNavigationOnClickListener { finish() }
        binding.rvReminders.layoutManager = LinearLayoutManager(this)

        loadDueTreatments()
    }

    private fun loadDueTreatments() {
        val now = LocalTime.now().format(fmt)
        val userId = auth.currentUser?.uid ?: return

        db.collection("treatments").document(userId)
            .collection("userTreatments")
            .get()
            .addOnSuccessListener { snap ->
                val dueList = snap.documents.mapNotNull { doc ->
                    val t = doc.toObject(Treatment::class.java)?.apply { id = doc.id }
                    t?.takeIf { treatment -> treatment.times.contains(now) }
                }.toMutableList()

                binding.rvReminders.adapter = ReminderAdapter(dueList) { treatment, choice ->

                    db.collection("treatments").document(userId)
                        .collection("userTreatments").document(treatment.id)
                        .collection("notifications").add(mapOf(
                            "timestamp" to com.google.firebase.Timestamp.now(),
                            "choice" to choice
                        ))
                    (binding.rvReminders.adapter as ReminderAdapter).removeItem(treatment)
                }
            }
    }
}