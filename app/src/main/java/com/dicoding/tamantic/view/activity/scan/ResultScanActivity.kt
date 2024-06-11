package com.dicoding.tamantic.view.activity.scan

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.Alamat
import com.dicoding.tamantic.data.model.ScanModel
import com.dicoding.tamantic.data.response.ScanResponse
import com.dicoding.tamantic.databinding.ActivityResultScanBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.dicoding.tamantic.view.utils.getImageUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.util.UUID

class ResultScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val image = intent.getStringExtra("IMAGE_SCAN").toString()
        val imageUri = Uri.parse(image)
        val data = intent.getParcelableExtra<ScanResponse>("SCAN_KEY")
        setResult(image, data)
        setupAction(imageUri, data)
        setupView()
    }

    private fun setResult(
        image: String,
        data: ScanResponse?
    ) {
        Glide.with(binding.imageDetailScan).load(image).into(binding.imageDetailScan)
        binding.namaPenyakit.text = data?.result
        binding.explantation.text = data?.explanation

        val convidenceScore = Math.round(data?.confidenceScore!!)
        val animator = ValueAnimator.ofInt(0, convidenceScore)
        animator.duration = 5000
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            binding.circularProgressBar.progress = animatedValue
            binding.convidenceScore.text = "$animatedValue%"

            when {
                animatedValue < 30 -> {
                    binding.convidenceScore.setTextColor(
                        ContextCompat.getColor(
                            this, R.color
                                .mred_600
                        )
                    )
                }

                animatedValue in 30..60 -> {
                    binding.convidenceScore.setTextColor(
                        ContextCompat.getColor(
                            this, R.color
                                .orange_500
                        )
                    )
                }

                else -> {
                    binding.convidenceScore.setTextColor(
                        ContextCompat.getColor(
                            this, R.color
                                .green_A700
                        )
                    )
                }
            }
        }
        animator.start()
    }

    private fun saveResult(image: Uri, data: ScanResponse?) {
        val storageReference =
            FirebaseStorage.getInstance().reference.child("images-result-scan/${UUID.randomUUID()}.jpg")
        storageReference.putFile(image).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                val fromId = FirebaseAuth.getInstance().uid.toString()
                val ref =
                    FirebaseDatabase.getInstance().getReference("/history-scan/$fromId/").push()

                ref.get().addOnSuccessListener { snapshot ->
                    val result = ScanModel(
                        ref.key!!, imageUrl, data?.result!!, data.explanation!!,
                        data.confidenceScore!!
                    )
                    ref.setValue(result).addOnSuccessListener {
                        popupAlertSuccess("Hasil berhasil disimpan")
                    }
                }
            }


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

        window.statusBarColor = Color.TRANSPARENT
    }

    private fun popupAlertSuccess(msg: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)
        title.text = "Berhasil"
        message.text = msg
    }

    private fun setupAction(image: Uri, data: ScanResponse?) {
        binding.actionBack.setOnClickListener { onBackPressed() }

        binding.actionSave.setOnClickListener { saveResult(image, data) }
    }


}