package com.dicoding.tamantic.view.activity.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        setupAction()

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
}