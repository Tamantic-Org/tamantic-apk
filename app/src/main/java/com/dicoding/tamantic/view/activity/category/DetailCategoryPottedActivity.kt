package com.dicoding.tamantic.view.activity.category

import android.content.Intent
import android.os.Bundle
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
import com.dicoding.tamantic.databinding.ActivityDetailCategoryPottedBinding
import com.dicoding.tamantic.view.activity.cart.CartListActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.CategoryViewModel

class DetailCategoryPottedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailCategoryPottedBinding
    private val categoryViewModel by viewModels<CategoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: MarketAdapter
    private lateinit var recylerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCategoryPottedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }
        binding.actionToCart.setOnClickListener {
            val intent = Intent(this, CartListActivity::class.java)
            startActivity(intent)
        }

        categoryViewModel.getSession().observe(this) { auth ->
            auth?.token.let {
                categoryViewModel.getProduct(auth.token, "Potted")
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
    }
}