package com.dicoding.tamantic.view.activity.flowProduct

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
}