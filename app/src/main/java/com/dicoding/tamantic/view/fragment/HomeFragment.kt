package com.dicoding.tamantic.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils.replace
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.ProductAdapter
import com.dicoding.tamantic.databinding.FragmentHomeBinding
import com.dicoding.tamantic.view.activity.cart.CartListActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryAlatActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryBungaActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryHiasActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryIndoorActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryMediaActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryOutdoorActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryPohonActivity
import com.dicoding.tamantic.view.activity.category.DetailCategoryPottedActivity
import com.dicoding.tamantic.view.activity.search.SearchActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.HomeViewModel
import com.google.android.play.integrity.internal.s

class HomeFragment : Fragment(R.layout.fragment_home) {

    //halaman home

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var recylerView: RecyclerView

    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.getSession().observe(viewLifecycleOwner) { auth ->
            auth?.token.let {
                homeViewModel.getProduct(auth.token)
            }
        }

        adapter = ProductAdapter(listOf())

        homeViewModel.productData.observe(requireActivity()){
            adapter.dataProduct = it!!
            adapter.notifyDataSetChanged()
            showLoading(false)
        }

        recylerView = binding.rvBestProduct
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)

        recylerView.layoutManager = layoutManager
        recylerView.adapter = adapter

        setupAction()

        binding.actionSearch.setOnEditorActionListener { v , actionId , event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {

                val name= v.text.toString()

                val intent = Intent(requireActivity(), SearchActivity::class.java)
                intent.putExtra("KEY_WORD", name)
                startActivity(intent)

                true
            } else {
                false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupAction() {

        binding.actionToCart.setOnClickListener {
            val intent = Intent(this.context, CartListActivity::class.java )
            startActivity(intent)
        }

        binding.actionToIndoor.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryIndoorActivity::class.java)
            startActivity(indoorActivity)
        }

        binding.actionToOutdoor.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryOutdoorActivity::class.java)
            startActivity(indoorActivity)
        }

        binding.actionToHias.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryHiasActivity::class.java)
            startActivity(indoorActivity)
        }
        binding.actionToBunga.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryBungaActivity::class.java)
            startActivity(indoorActivity)
        }

        binding.actionToPohon.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryPohonActivity::class.java)
            startActivity(indoorActivity)
        }

        binding.actionToPot.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryPottedActivity::class.java)
            startActivity(indoorActivity)
        }

        binding.actionToMedia.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryMediaActivity::class.java)
            startActivity(indoorActivity)
        }

        binding.actionToAlat.setOnClickListener{
            val indoorActivity = Intent(this.context , DetailCategoryAlatActivity::class.java)
            startActivity(indoorActivity)
        }

    }

}