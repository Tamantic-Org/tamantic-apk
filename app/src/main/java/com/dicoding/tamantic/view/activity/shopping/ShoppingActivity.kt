package com.dicoding.tamantic.view.activity.shopping

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.MarketAdapter
import com.dicoding.tamantic.data.adapter.ProductAdapter
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.data.response.ProductsResponse
import com.dicoding.tamantic.data.retrofit.ApiConfig
import com.dicoding.tamantic.databinding.ActivityTokoBinding
import com.dicoding.tamantic.view.activity.cart.CartListActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.CategoryViewModel
import com.dicoding.tamantic.view.viewModel.HomeViewModel
import com.dicoding.tamantic.view.viewModel.MarketViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTokoBinding
    private val marketViewModel by viewModels<MarketViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: MarketAdapter
    private lateinit var recylerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoBinding.inflate(layoutInflater)
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
    }

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


}


