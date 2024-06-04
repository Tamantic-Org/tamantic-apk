package com.dicoding.tamantic.view.activity.payment

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.tamantic.R
import com.dicoding.tamantic.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)
        binding.totalPayment.text = "Total Harga: Rp $totalPrice"

    }
}