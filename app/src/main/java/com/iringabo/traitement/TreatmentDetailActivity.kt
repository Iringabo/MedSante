package com.iringabo.traitement

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.databinding.ActivityTreatmentDetailBinding
import com.iringabo.traitement.model.Treatment

@Suppress("DEPRECATION")
class TreatmentDetailActivity : AppCompatActivity() {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var treatmentId: String
    private lateinit var binding: ActivityTreatmentDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreatmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer l'ID du traitement
        treatmentId = intent.getStringExtra("treatment_id")
            ?: throw IllegalArgumentException("Treatment ID missing")

        binding.detailToolbar.setNavigationOnClickListener { onBackPressed() }

        // Charger les données du traitement
        db.collection("treatments")
            .document(auth.currentUser!!.uid)
            .collection("userTreatments")
            .document(treatmentId)
            .get()
            .addOnSuccessListener { doc ->
                doc.toObject(Treatment::class.java)?.let { t ->
                    binding.tvDetailMedName.text    = t.medName
                    binding.tvDetailDosage.text      = "Dosage: ${t.dosage}"
                    binding.tvDetailQuantity.text    = "Quantité: ${t.quantity}"
                    binding.tvDetailDates.text       = "${t.startDate} → ${t.endDate}"
                    binding.tvDetailTimes.text       = "Horaires: ${t.times.joinToString(", ")}"
                    binding.tvDetailRemarks.text     = "Remarques: ${t.remarks}"
                }
            }
    }
}
