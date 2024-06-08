package com.dicoding.tamantic.view.activity.plant

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.PlantAdapter
import com.dicoding.tamantic.data.model.MyPlant
import com.dicoding.tamantic.data.model.Plant
import com.dicoding.tamantic.databinding.ActivityDetailPlantBinding

class DetailPlantActivity : AppCompatActivity() {

    private lateinit var binding:  ActivityDetailPlantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<MyPlant>("MY_PLANT")
        setMyPlant(data)
        setupAction()
        setupView()
    }

    private fun setMyPlant(data: MyPlant?) {
        binding.descPlant.text = data?.description
        Glide.with(binding.imageDetailMyplant).load(data?.image).into(binding.imageDetailMyplant)

        binding.matahari.text = "${data?.matahari} %"
        binding.pupuk.text = "${data?.pupuk} %"
        binding.air.text = "${data?.air} %"
        binding.gunting.text = "${data?.potong} %"
    }

    private fun setupAction() {
        binding.actionBack.setOnClickListener { onBackPressed() }
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