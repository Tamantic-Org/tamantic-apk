package com.dicoding.tamantic.view.activity.plant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.tamantic.R
import com.dicoding.tamantic.databinding.ActivityDetailMyPlantBinding
import com.dicoding.tamantic.databinding.ActivityDetailScanBinding

class DetailMyPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMyPlantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMyPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }
    }
}