package com.dicoding.tamantic.view.activity.scan

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.response.ScanResponse
import com.dicoding.tamantic.databinding.ActivityResultScanBinding

class ResultScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val image = intent.getStringExtra("IMAGE_SCAN").toString()
        Log.d("DATA IMAGE", image)
        val data = intent.getParcelableExtra<ScanResponse>("SCAN_KEY")

        setResult(image, data)
        setupAction()
        setupView()
    }

    private fun setResult(image: String, data: ScanResponse?) {
        binding.imageDetailScan
        Glide.with(binding.imageDetailScan).load(image).into(binding.imageDetailScan)

        binding.namaPenyakit.text = data!!.result
        binding.explantation.text = data.explanation

        val convidenceScore = Math.round(data.confidenceScore!!)
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

    private fun setupView() {
        binding.actionBack.setOnClickListener { onBackPressed() }
    }

    private fun setupAction() {
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


}