package com.dicoding.tamantic.view.activity.plant

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.MyPlant
import com.dicoding.tamantic.databinding.ActivityDetailMyPlantBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailMyPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMyPlantBinding
    private lateinit var alarm: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMyPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }

        val image = intent.getStringExtra("IMAGE_PLANT").toString()
        val deskripsi = intent.getStringExtra("DESKRIPSI_PLANT").toString()

        Log.d("DATA", image)

        setPlan(image, deskripsi)
        setupAction(image, deskripsi)
        setupView()
    }


    private fun setupAction(image: String, deskripsi: String) {
        binding.actionAddMyPlant.setOnClickListener {
            addMyPlant(image, deskripsi)
        }
    }

    private fun addMyPlant(image: String, deskripsi: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/my-plant/$fromId").push()
        ref.get().addOnSuccessListener {
            val plant = MyPlant(ref.key!!, image, deskripsi, "", "", "", "")
            ref.setValue(plant).addOnSuccessListener {
                Toast.makeText(this, "Berhasil menambahkan tanaman", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPlan(image: String, deskripsi: String) {
//        binding.namaPlant.text = "Kamboja"
        binding.descPlant.text = deskripsi
        Glide.with(binding.imageDetailMyplant).load(image).into(binding.imageDetailMyplant)
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