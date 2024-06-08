package com.dicoding.tamantic.view.activity.search

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.SearchAdapter
import com.dicoding.tamantic.databinding.ActivitySearchBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var recylerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        val name = intent.getStringExtra("KEY_WORD")
        binding.actionSearch.setText(name)
        binding.kamuMencari.text = "Kamu mencari : $name"

        adapter = SearchAdapter(listOf())

        searchViewModel.setProduct(name.toString())

        searchViewModel.nameProduct.observe(this){
            adapter.dataProduct = it!!
            adapter.notifyDataSetChanged()
        }

        searchViewModel.isDataEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                binding.rvMarket.visibility = View.VISIBLE
                binding.textEmpty.visibility = View.GONE
            } else {
                binding.rvMarket.visibility = View.GONE
                binding.textEmpty.visibility = View.VISIBLE
            }
        }

        recylerView = binding.rvMarket
        recylerView.layoutManager = LinearLayoutManager(this)
        recylerView.adapter = adapter

        setupAction()
        setupView()
    }

    private fun setupAction() {

        binding.actionBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.actionSearch.setOnEditorActionListener { v , actionId , event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {

                val name= v.text.toString()

                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("KEY_WORD", name)
                startActivity(intent)

                true
            } else {
                false
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

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val statusBarColor = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(this, android.R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> ContextCompat.getColor(this, R.color.white)
            else -> ContextCompat.getColor(this, R.color.white)
        }

        window.statusBarColor = statusBarColor
    }
}