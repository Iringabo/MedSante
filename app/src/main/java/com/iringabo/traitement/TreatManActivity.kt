package com.iringabo.traitement

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.model.Treatment
import java.util.Calendar
import java.util.Locale
import androidx.core.view.isVisible

class TreatManActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_treatment)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etMedName: TextInputEditText = findViewById(R.id.et_med_name)
        val etDosage: TextInputEditText = findViewById(R.id.et_dosage)
        val etQuantity: TextInputEditText = findViewById(R.id.et_quantity)
        val spinnerTimes = findViewById<Spinner>(R.id.spinner_times_per_day)

        // Time pickers
        val etTime1: TextInputEditText = findViewById(R.id.et_time_1)
        val etTime2: TextInputEditText = findViewById(R.id.et_time_2)
        val etTime3: TextInputEditText = findViewById(R.id.et_time_3)
        val etTime4: TextInputEditText = findViewById(R.id.et_time_4)
        val etTime5: TextInputEditText = findViewById(R.id.et_time_5)

        val etStartDate  = findViewById<EditText>(R.id.et_start_date)
        val etEndDate    = findViewById<EditText>(R.id.et_end_date)
        val etRemarks    = findViewById<EditText>(R.id.et_remarks)
        val btnSave      = findViewById<Button>(R.id.btn_save)

        // Show/hide time fields based on spinner
        spinnerTimes.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val count = parent.getItemAtPosition(position).toString().toInt()
                val fields = listOf(etTime1, etTime2, etTime3, etTime4, etTime5)
                fields.forEachIndexed { index, et ->
                    et.parent?.let { (it as android.view.View).visibility = if(index < count) android.view.View.VISIBLE else android.view.View.GONE }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Date & Time pickers
        fun showTimePicker(et: EditText) {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                et.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }
        listOf(etTime1, etTime2, etTime3, etTime4, etTime5).forEach { et ->
            et.setOnClickListener { showTimePicker(et) }
        }

        fun showDatePicker(et: EditText) {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, mo, d ->
                et.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", y, mo+1, d))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        etStartDate.setOnClickListener { showDatePicker(etStartDate) }
        etEndDate.setOnClickListener   { showDatePicker(etEndDate) }

        btnSave.setOnClickListener {
            var medName   = etMedName.text.toString().trim()
            var dosage    = etDosage.text.toString().toIntOrNull() ?: 0
            var quantity  = etQuantity.text.toString().toIntOrNull() ?: 0
            var times     = mutableListOf<String>().apply {
                listOf(etTime1, etTime2, etTime3, etTime4, etTime5).forEach { if(it.isVisible) add(it.text.toString()) }
            }
            var startDate = etStartDate.text.toString()
            var endDate   = etEndDate.text.toString()
            var remarks   = etRemarks.text.toString()
            val treatment = Treatment(
                medName   = medName,
                dosage    = dosage,
                quantity  = quantity,
                times     = times,
                startDate = startDate,
                endDate   = endDate,
                remarks   = remarks
            )

            val uid = auth.currentUser?.uid
                ?: return@setOnClickListener Toast
                    .makeText(this, "Non connecté", Toast.LENGTH_SHORT)
                    .show()

            db.collection("treatments").document(uid)
                .collection("userTreatments")
                .add(treatment)
                .addOnSuccessListener { docRef ->
                    docRef.update("id", docRef.id)
                    Toast.makeText(this, "Enregistré", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }
}