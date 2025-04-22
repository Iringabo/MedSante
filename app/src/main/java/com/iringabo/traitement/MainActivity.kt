package com.iringabo.traitement

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.transition.TransitionInflater


class MainActivity : ComponentActivity() {
    private val splashscreen: Long = 5000
    override fun onCreate(savedInstanceState: Bundle?) {
        val slideTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide)
        window.enterTransition = slideTransition
        window.exitTransition = slideTransition
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val imageView: ImageView = findViewById(R.id.logo_img)
        val text1: TextView = findViewById(R.id.text1)
        val text2:TextView = findViewById(R.id.text2)

        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        imageView.setAnimation(topAnimation)
        text1.setAnimation(bottomAnimation)
        text2.setAnimation(bottomAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
            finish()
        }, splashscreen)
    }
}