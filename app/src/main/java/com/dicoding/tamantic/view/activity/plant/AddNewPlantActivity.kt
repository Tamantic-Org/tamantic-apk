package com.dicoding.tamantic.view.activity.plant

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.dicoding.tamantic.R
import com.dicoding.tamantic.databinding.ActivityAddNewPlantBinding
import com.google.android.play.integrity.internal.c
import com.yalantis.ucrop.UCrop
import java.io.File

class AddNewPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewPlantBinding

    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentImageUri = intent.getStringExtra("imageUri")?.let {
            Uri.parse(it)
        }

        showImage(currentImageUri)

        setupAction()
        setupView()
    }

    private fun setupAction() {
        binding.actionToEdit.setOnClickListener { startUCrop(currentImageUri!!) }
        binding.actionBack.setOnClickListener { onBackPressed() }

        binding.actionSend.setOnClickListener {
            val deskripsi = binding.inputDeskripsi.text.toString()
            val intent = Intent(this, DetailMyPlantActivity::class.java)
            intent.putExtra("IMAGE_PLANT", currentImageUri.toString())
            intent.putExtra("DESKRIPSI_PLANT", deskripsi)
            startActivity(intent)
        }
    }

    private fun showImage(image: Uri?) {
        binding.imageScan.setImageURI(image)
    }

    private fun startUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)
            .start(this)
        Log.d("Success Take Picture", "success for take picture")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            showImage(resultUri)

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Toast.makeText(this, "failed to crop image $cropError", Toast.LENGTH_SHORT).show()
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