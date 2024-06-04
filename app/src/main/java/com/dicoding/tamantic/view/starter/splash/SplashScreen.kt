package com.dicoding.tamantic.view.starter.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.tamantic.R
import com.dicoding.tamantic.view.starter.introduction.IntroductionActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_spash_screen)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_splash_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val splash = findViewById<ImageView>(R.id.logo)

        val inten = Intent(this, IntroductionActivity::class.java)

        splash.animate().setDuration(5000).alpha(1f).withEndAction {
            startActivity(inten)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }


    }
}