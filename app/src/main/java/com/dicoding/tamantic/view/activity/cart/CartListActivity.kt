package com.dicoding.tamantic.view.activity.cart

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.data.adapter.CartAdapter
import com.dicoding.tamantic.data.model.Alamat
import com.dicoding.tamantic.data.model.ProductModel
import com.dicoding.tamantic.databinding.ActivityCartListBinding
import com.dicoding.tamantic.view.activity.payment.PaymentActivity
import com.dicoding.tamantic.view.main.MainActivity
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

    private val addresses = ArrayList<String>()

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
        getAddress()
        setupAction()
        setupView()
    }

    private fun setupAction() {
        binding.getOther.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAddress() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/address/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val alamat = snapshot.getValue(Alamat::class.java)
                val address = alamat?.address
                address?.let { addresses.add(it) }

                val adapter = ArrayAdapter(
                    this@CartListActivity, R.layout.simple_dropdown_item_1line,
                    addresses
                )
                binding.autoComplete.setAdapter(adapter)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.autoComplete.threshold = 1
    }

    private fun refreshRv() {
        productList.clear()
        totalPrice = 0
        lastProduct.values.forEach {
            productList.add(it)
            totalPrice += it.total
        }
        binding.jumlahPesananCart.text = "${productList.size} Pesanan"
        val formattedTotalPrice =
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice)
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
            val selectedAddress = binding.autoComplete.text.toString()

            if (totalPrice == 0) {
                Toast.makeText(this, "Keranjang harus Disi terlebih dahulu", Toast.LENGTH_SHORT)
                    .show()
            } else if (selectedAddress == "") {
                Toast.makeText(this, "Silahkan pilih alamat terlebih dahulu", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val intent = Intent(this, PaymentActivity::class.java)
                moveToPacked()
                intent.putExtra("TOTAL_PRICE", totalPrice)
                intent.putExtra("ADDRESS", selectedAddress)
                startActivity(intent)
            }
        }

    }

    private fun moveToPacked() {
        val fromId = FirebaseAuth.getInstance().uid
        val cartRef = FirebaseDatabase.getInstance().getReference("/cart/$fromId")
        val packedRef = FirebaseDatabase.getInstance().getReference("/packed/$fromId")

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(ProductModel::class.java)
                    product?.let {
                        it.status = "dikemas"
                        packedRef.child(productSnapshot.key!!).setValue(it)
                    }
                }
                cartRef.removeValue()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartListActivity", "Database error: ${error.message}")
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
        supportActionBar?.hide()
        window.statusBarColor = Color.TRANSPARENT
    }
}