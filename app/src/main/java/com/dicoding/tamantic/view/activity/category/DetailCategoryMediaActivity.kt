package com.dicoding.tamantic.view.activity.category

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.MarketAdapter
import com.dicoding.tamantic.databinding.ActivityDetailCategoryIndoorBinding
import com.dicoding.tamantic.databinding.ActivityDetailCategoryMediaBinding
import com.dicoding.tamantic.view.activity.cart.CartListActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.CategoryViewModel

class DetailCategoryMediaActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailCategoryMediaBinding
    private val categoryViewModel by viewModels<CategoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: MarketAdapter
    private lateinit var recylerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCategoryMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }
        binding.actionToCart.setOnClickListener {
            val intent = Intent(this, CartListActivity::class.java)
            startActivity(intent)
        }

        categoryViewModel.getSession().observe(this) { auth ->
            auth?.token.let {
                categoryViewModel.getProduct(auth.token, "Media")
            }
        }

        adapter = MarketAdapter(listOf())

        categoryViewModel.productData.observe(this){
            adapter.dataProduct = it!!
            adapter.notifyDataSetChanged()
        }

        recylerView = binding.rvCategoryList
        recylerView.layoutManager = LinearLayoutManager(this)
        recylerView.adapter = adapter

        setupView()
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