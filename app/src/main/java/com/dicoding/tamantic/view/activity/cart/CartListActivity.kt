package com.dicoding.tamantic.view.activity.cart

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.data.adapter.CartAdapter
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.data.model.ProductModel
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.databinding.ActivityCartListBinding
import com.dicoding.tamantic.view.activity.payment.PaymentActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale

class CartListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartListBinding
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recylerView: RecyclerView
    private var productList = mutableListOf<ProductModel>()
    private var lastProduct = HashMap<String, ProductModel>()
    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }

        recylerView = binding.rvCartList
        recylerView.layoutManager = LinearLayoutManager(this)

        cartAdapter = CartAdapter(productList)
        recylerView.adapter = cartAdapter

        binding.jumlahPesananCart.text = "${productList.size} Pesanan"
        getCartProduct()
        setupView()
    }

    private fun refreshRv() {
        productList.clear()
        totalPrice = 0
        lastProduct.values.forEach {
            productList.add(it)
            totalPrice += it.total
        }
        binding.jumlahPesananCart.text = "${productList.size} Pesanan"
        val formattedTotalPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice)
        binding.totalHarga.text = "$formattedTotalPrice"
        cartAdapter.notifyDataSetChanged()
    }

    private fun getCartProduct() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/cart/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(ProductModel::class.java)?.let {
                    lastProduct[snapshot.key!!] = it
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
                Log.e("CartListActivity", "Database error: ${error.message}")
            }

        })

        binding.actionToBayar.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("TOTAL_PRICE" , totalPrice)
            startActivity(intent)
        }

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