package com.iringabo.traitement

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.transition.TransitionInflater
import android.util.Log
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        val slideTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide)
        window.enterTransition = slideTransition
        window.exitTransition = slideTransition

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val callSignUp:Button = findViewById(R.id.sign_up_button)
        val loginButton:Button = findViewById(R.id.login_button)
        val userEmail: TextInputEditText = findViewById(R.id.username_input_lg)
        val userPass: TextInputEditText = findViewById(R.id.password_input_lg)
        loginButton.setOnClickListener{
            val email = userEmail.text.toString()
            val password = userPass.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w("SignUp", "signInWithEmailAndPassword:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


        }
        }
        callSignUp.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onStart(){
        if(auth.currentUser != null){
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        super.onStart()

    }
}
