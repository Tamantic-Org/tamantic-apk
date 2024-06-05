package com.dicoding.tamantic.view.activity.plant

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.tamantic.databinding.ActivityAddNewPlantBinding
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
            val intent = Intent(this, DetailMyPlantActivity::class.java)
            startActivity(intent)
            finish()
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
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}