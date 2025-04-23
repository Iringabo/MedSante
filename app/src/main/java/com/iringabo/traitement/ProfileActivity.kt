package com.iringabo.traitement

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iringabo.traitement.model.Treatment
import java.io.File

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var prefs: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        setupProfile()
        setupPreferences()
        setupButtons()


    }

    private fun setupProfile() {
        val user = auth.currentUser
        user?.let {
            findViewById<TextView>(R.id.tv_profile_email).text = it.email
            // Load additional user data from Firestore
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("name") ?: "No name set"
                    findViewById<TextView>(R.id.tv_profile_name).text = name
                }
        }

        findViewById<MaterialButton>(R.id.btn_edit_profile).setOnClickListener {
            Toast.makeText(this, "Edit profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPreferences() {
        val soundSwitch = findViewById<SwitchMaterial>(R.id.switch_sound)
        val vibrationSwitch = findViewById<SwitchMaterial>(R.id.switch_vibration)

        // Load saved preferences
        soundSwitch.isChecked = prefs.getBoolean("sound_enabled", true)
        vibrationSwitch.isChecked = prefs.getBoolean("vibration_enabled", true)

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sound_enabled", isChecked).apply()
        }

        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("vibration_enabled", isChecked).apply()
        }

        findViewById<MaterialButton>(R.id.btn_export_data).setOnClickListener {
            exportDataToCSV()
        }
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.btn_faq).setOnClickListener {
//            Toast.makeText(this, "FAQ feature coming soon", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FaqActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btn_contact).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:wise.discovery@iringabo.com")
                putExtra(Intent.EXTRA_SUBJECT, "App Support Request")
            }
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

        findViewById<MaterialButton>(R.id.btn_logout).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun exportDataToCSV() {
        val uid = auth.currentUser?.uid ?: return
        val csvHeader = "Medicine,Date,Time,Dosage\n"
        val csvContent = StringBuilder(csvHeader)

        db.collection("treatments")
            .document(uid)
            .collection("userTreatments")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val treatment = document.toObject(Treatment::class.java)
                    csvContent.append("${treatment.medName},${treatment.startDate}," +
                            "${treatment.times.joinToString()},${treatment.dosage}\n")
                }

                val file = File(getExternalFilesDir(null), "treatments_export.csv")
                file.writeText(csvContent.toString())

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                        this@ProfileActivity,
                        "${packageName}.provider",
                        file
                    ))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Export Data"))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Export failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
}
