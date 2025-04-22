package com.iringabo.traitement

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.model.Treatment

@Suppress("DEPRECATION")
class EditTreatmentActivity : AppCompatActivity() {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var treatmentId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_treatment)

        treatmentId = intent.getStringExtra("treatment_id")
            ?: throw IllegalArgumentException("Treatment ID missing")

        val edit_toolbar = findViewById<MaterialToolbar>(R.id.edit_toolbar)
        val etMedName = findViewById<TextInputEditText>(R.id.etMedName)
        val etStartDate = findViewById<TextInputEditText>(R.id.etStartDate)
        val etEndDate = findViewById<TextInputEditText>(R.id.etEndDate)
        val btnSave = findViewById<MaterialToolbar>(R.id.btnSave)
        edit_toolbar.setNavigationOnClickListener { onBackPressed() }

        // Load existing data
        db.collection("treatments")
            .document(auth.currentUser!!.uid)
            .collection("userTreatments")
            .document(treatmentId)
            .get()
            .addOnSuccessListener { doc ->
                doc.toObject(Treatment::class.java)?.let { t ->

                    etMedName.setText(t.medName)
                    etStartDate.setText(t.startDate)
                    etEndDate.setText(t.endDate)
                }
            }

        btnSave.setOnClickListener {
            val updated = Treatment(
                id = treatmentId,
                medName = etMedName.text.toString(),
                startDate = etStartDate.text.toString(),
                endDate = etEndDate.text.toString()
            )
            db.collection("treatments")
                .document(auth.currentUser!!.uid)
                .collection("userTreatments")
                .document(treatmentId)
                .set(updated)
                .addOnSuccessListener {
                    Toast.makeText(this, "Mis à jour avec succès", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur de mise à jour", Toast.LENGTH_SHORT).show()
                }
        }
    }
}