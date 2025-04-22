package com.iringabo.traitement

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.model.Treatment

class TreatmentsActivity : ComponentActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { FirebaseFirestore.getInstance() }

    private lateinit var rvTreatments: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treatments)

        rvTreatments = findViewById(R.id.rv_treatments)
        rvTreatments.layoutManager = LinearLayoutManager(this)

        loadAndShowTreatments()
    }

    private fun loadAndShowTreatments() {
        fetchUserTreatments { list ->
            rvTreatments.adapter = TreatmentsAdapter(
                list,
                onView   = { tr -> openDetail(tr.id) },
                onEdit   = { tr -> openEdit(tr.id) },
                onDelete = { tr -> confirmDelete(tr) }
            )
        }
    }

    private fun fetchUserTreatments(onResult: (List<Treatment>) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) return onResult(emptyList())

        db.collection("treatments")
            .document(uid)
            .collection("userTreatments")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { doc ->
                    val t = doc.toObject(Treatment::class.java)!!
                    t.id = doc.id
                    t
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    private fun openDetail(treatmentId: String) {
        Intent(this, TreatmentDetailActivity::class.java)
            .putExtra("treatment_id", treatmentId)
            .also(::startActivity)
    }

    private fun openEdit(treatmentId: String) {
        Intent(this, EditTreatmentActivity::class.java)
            .putExtra("treatment_id", treatmentId)
            .also(::startActivity)
    }

    private fun confirmDelete(tr: Treatment) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer ce traitement ?")
            .setMessage("Voulez‑vous vraiment supprimer « ${tr.medName} » ?")
            .setPositiveButton("Oui") { _, _ ->
                db.collection("treatments")
                    .document(auth.currentUser!!.uid)
                    .collection("userTreatments")
                    .document(tr.id)
                    .delete()
                    .addOnSuccessListener {
                        // simply reload the list and swap the adapter
                        loadAndShowTreatments()
                    }
            }
            .setNegativeButton("Non", null)
            .show()
    }
}
