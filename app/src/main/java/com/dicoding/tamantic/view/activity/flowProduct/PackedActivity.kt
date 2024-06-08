package com.dicoding.tamantic.view.activity.flowProduct

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.CartAdapter
import com.dicoding.tamantic.data.adapter.PackedAdapter
import com.dicoding.tamantic.data.model.ProductModel
import com.dicoding.tamantic.databinding.ActivityPackedBinding
import com.dicoding.tamantic.view.activity.payment.PaymentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class PackedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPackedBinding

    private lateinit var cartAdapter: PackedAdapter
    private lateinit var recylerView: RecyclerView
    private var productList = mutableListOf<ProductModel>()
    private var lastProduct = HashMap<String, ProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }

        recylerView = binding.rvPacked
        recylerView.layoutManager = LinearLayoutManager(this)

        cartAdapter = PackedAdapter(productList)
        recylerView.adapter = cartAdapter

        getProduct()
        setupView()
    }

    private fun refreshRv() {
        productList.clear()
        lastProduct.values.forEach {
            productList.add(it)
        }
        cartAdapter.notifyDataSetChanged()
    }

    private fun getProduct() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/packed/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(ProductModel::class.java)?.let {
                    lastProduct[snapshot.key!!] = it
                    Log.d("DATA",it.toString())
                    runOnUiThread { refreshRv() }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(ProductModel::class.java)?.let {
                    lastProduct[snapshot.key!!] = it
                    runOnUiThread { refreshRv() }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                lastProduct.remove(snapshot.key)
                runOnUiThread { refreshRv() }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PackedActivity", "Database error: ${error.message}")
            }

        })
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