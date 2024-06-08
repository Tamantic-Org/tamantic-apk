package com.dicoding.tamantic.view.activity.payment

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.dicoding.tamantic.BuildConfig.BASE_URL
import com.dicoding.tamantic.databinding.ActivityPaymentBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.external.UiKitApi
import kotlinx.coroutines.withContext

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMidtrans()

        val totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)
        val address = intent.getStringExtra("ADDRESS")

        binding.totalPayment.text = "Total Harga: Rp $totalPrice"
        binding.address.text = "Produk akan dikirimkan ke alamat $address"

        setupView()

        binding.selesai.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setMidtrans() {
        UiKitApi.Builder()
            .withMerchantClientKey("SB-Mid-client-dljmNTDZgeIccAfw")
            .withContext(this)
            .withMerchantUrl(BASE_URL)
            .enableLog(true)
            .withColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .build()
        setLocaleNew("id")
    }

    private fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.setDecorFitsSystemWindows(false)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        supportActionBar?.hide()
        window.statusBarColor = Color.TRANSPARENT
    }
}