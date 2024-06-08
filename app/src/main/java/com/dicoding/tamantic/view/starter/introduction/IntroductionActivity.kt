package com.dicoding.tamantic.view.starter.introduction

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.pref.dataStore
import com.dicoding.tamantic.databinding.ActivityIntroductionBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.starter.login.LoginActivity
import com.dicoding.tamantic.view.viewModel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class IntroductionActivity : AppCompatActivity() {

    private lateinit var binding :  ActivityIntroductionBinding

    private val darkViewModel by viewModels<ThemeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAuthAndLogin()
        playAnimation()

        val button_started = binding.introductionBtn
        val intent = Intent(this, LoginActivity::class.java)

        button_started.setOnClickListener {
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }

        darkViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        setupView()

    }

    private fun checkAuthAndLogin() {
        lifecycleScope.launch {
            val isAuthValid = authCheck()
            val isLoginValid = loginValidation()

            if (isAuthValid || isLoginValid) {
                Log.d("MainActivity", "checkAuthAndLogin: $isAuthValid - $isLoginValid")
                val intent = Intent(this@IntroductionActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }


    private suspend fun authCheck(): Boolean {
        val token = dataStore.data.map { pref ->
            pref[stringPreferencesKey("token")]
        }.firstOrNull()
        Log.d(this.toString(), "authCheck: $token")
        return !token.isNullOrEmpty()
    }

    private fun loginValidation(): Boolean {
        val uid = FirebaseAuth.getInstance().uid
        return uid != null
    }

    private fun playAnimation(){
        val logo = ObjectAnimator.ofFloat(binding.introductionImg, View.ALPHA, 1f).setDuration(500)
        val slogan = ObjectAnimator.ofFloat(binding.introduction1, View.ALPHA,1f).setDuration(500)
        val slogan2 = ObjectAnimator.ofFloat(binding.introduction2, View.ALPHA, 1f).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.introductionBtn, View.ALPHA, 1f).setDuration(500)
        val shadow = ObjectAnimator.ofFloat(binding.shadowImg, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(logo,slogan,slogan2,btn, shadow)
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val statusBarColor = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(this, android.R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> ContextCompat.getColor(this, R.color.white)
            else -> ContextCompat.getColor(this, R.color.white)
        }

        window.statusBarColor = statusBarColor
    }

}