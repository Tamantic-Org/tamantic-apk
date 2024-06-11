package com.dicoding.tamantic.view.activity.category

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.MarketAdapter
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.databinding.ActivityDetailCategoryIndoorBinding
import com.dicoding.tamantic.databinding.ActivityDetailCategoryPohonBinding
import com.dicoding.tamantic.view.activity.cart.CartListActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.CategoryViewModel

class DetailCategoryPohonActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailCategoryPohonBinding
    private val categoryViewModel by viewModels<CategoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: MarketAdapter
    private lateinit var recylerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCategoryPohonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }
        binding.actionToCart.setOnClickListener {
            val intent = Intent(this, CartListActivity::class.java)
            startActivity(intent)
        }

        categoryViewModel.getSession().observe(this) { auth ->
            auth?.token.let {
                categoryViewModel.getProduct(auth.token, "Pohon")
            }
        }

        adapter = MarketAdapter(listOf())
        recylerView = binding.rvCategoryList
        recylerView.layoutManager = LinearLayoutManager(this)
        recylerView.adapter = adapter


        categoryViewModel.productData.observe(this) {
            it?.let {
                adapter.dataProduct = it
                adapter.notifyDataSetChanged()
                checkProductAvailability(it)
            }
        }

        setupView()
    }

    private fun checkProductAvailability(products: List<DataItem>) {
        if (products.isEmpty()) {
            binding.tvNoProduct.visibility = View.VISIBLE
        } else {
            binding.tvNoProduct.visibility = View.GONE
        }
        progressBar(false)
    }

    private fun progressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
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