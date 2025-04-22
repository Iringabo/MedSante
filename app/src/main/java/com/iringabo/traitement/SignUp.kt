package com.iringabo.traitement

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.enableEdgeToEdge
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUp : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        val callSignIn: Button = findViewById(R.id.sign_in)
        val callSignIn2: Button = findViewById(R.id.sign_up)
        val emailLayout: TextInputLayout = findViewById(R.id.email_input_layout)
        val emailEditText: TextInputEditText = emailLayout.findViewById(R.id.email_input)
        val passlayout: TextInputLayout = findViewById(R.id.password_input_layout_1)
        val passwordEditText: TextInputEditText = passlayout.findViewById(R.id.password_input_1)
        val passlayout2: TextInputLayout = findViewById(R.id.password_input_layout2)
        val secondPassword: TextInputEditText = passlayout2.findViewById(R.id.password_input)


        callSignIn2.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val secondp = secondPassword.text.toString()

            if (password != secondp) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (email.isEmpty() || password.isEmpty() || secondp.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(password.length < 6){
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("SignUp", "createUserWithEmail:success")
                            Toast.makeText(this, "User created successfully.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Log.w("SignUp", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        callSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
