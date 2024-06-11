package com.dicoding.tamantic.view.activity.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.data.adapter.MarketAdapter
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.databinding.ActivityShoppingBinding
import com.dicoding.tamantic.view.activity.cart.CartListActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.MarketViewModel
import java.text.NumberFormat
import java.util.Locale

class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingBinding
    private val marketViewModel by viewModels<MarketViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: MarketAdapter
    private lateinit var recylerView: RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<DataItem>("PRODUCT_KEY")

        getProductSelected(data)
        setupAction()

        marketViewModel.getSession().observe(this) { auth ->
            auth?.token.let {
                data?.owner?.let { it1 -> marketViewModel.getProductMarket(auth.token, it1) }
            }
        }

        adapter = MarketAdapter(listOf())

        marketViewModel.productData.observe(this){
            adapter.dataProduct = it!!
            adapter.notifyDataSetChanged()
        }

        recylerView = binding.rvMarket
        recylerView.layoutManager = LinearLayoutManager(this)
        recylerView.adapter = adapter

        setupView()
    }

    @SuppressLint("SetTextI18n")
    private fun getProductSelected(data: DataItem?) {
        binding.namaProduct.text = data?.name
        binding.rating.text = data?.rate.toString()
        val formattedTotalPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(data?.price)
        binding.priceProduct.text = formattedTotalPrice.toString()
        binding.sold.text = "${data?.sold} terjual"

        Glide.with(binding.backgroundMarket).load(data?.imageMarket).into(binding.backgroundMarket)
        Glide.with(binding.imageProductToko).load(data?.image).into(binding.imageProductToko)
    }

    private fun setupAction() {
        binding.actionBack.setOnClickListener { onBackPressed() }

        binding.actionToCart.setOnClickListener {
            val intent = Intent(this, CartListActivity::class.java)
            startActivity(intent)
        }
        binding.constToko2.setOnClickListener {
            val data = intent.getParcelableExtra<DataItem>("PRODUCT_KEY")
            val intent = Intent(this, DetailProductActivity::class.java)
            intent.putExtra("PRODUCT_KEY", data)
            startActivity(intent)
        }
    }

    private fun setupView() {
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
        supportActionBar?.hide()
        window.statusBarColor = Color.TRANSPARENT
    }


}


