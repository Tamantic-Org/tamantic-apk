package com.dicoding.tamantic.view.activity.shopping

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.ui.res.fontResource
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.alpha
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.MarketAdapter
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.data.model.ProductModel
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.databinding.ActivityDetailProductBinding
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.MarketViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<DataItem>("PRODUCT_KEY")

        getProductSelected(data)
        setCategoryProduct(data)
        setupAction(data)
    }

    private fun setCategoryProduct(data: DataItem?) {
        val categoryContainer = binding.categoryContainer
        categoryContainer.removeAllViews()

        val categories = data?.categories

        if (!categories.isNullOrEmpty()) {
            for (category in categories) {
                val categoryButton = AppCompatButton(this).apply {
                    text = category
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    background =
                        ContextCompat.getDrawable(context, R.drawable.background_rounded_button)
                    textSize = 11f
                    typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
                    isAllCaps = false
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(60),
                        dpToPx(50)
                    ).apply {
                        marginEnd = 8
                    }
                }
                categoryContainer.addView(categoryButton)
            }

        }
    }

    private fun getProductSelected(data: DataItem?) {
        val ivImage = binding.viewPagerProdImg
        val tvName = binding.tvProdName
        val tvPrice = binding.tvProdNewPrice
        val tvLocation = binding.tvProdLocationInfo
        val tvDesc = binding.tvProdDesc

        val name = data?.name
        val desc = data?.description
        val price = data?.price
        val location = data?.alamat
        val image = Uri.parse(data?.image)

        Glide.with(ivImage).load(image).into(ivImage)
        tvName.text = name
        tvPrice.text = "Rp " + price.toString()
        tvLocation.text = location
        tvDesc.text = desc
    }

    private fun setupAction(data: DataItem?) {
        binding.actionToBack.setOnClickListener { onBackPressed() }
        binding.actionToCart.setOnClickListener { addToCart(data) }
    }

    private fun addToCart(data: DataItem?) {
        val name = data?.name.toString()
        val desc = data?.description.toString()
        val owner = data?.owner.toString()
        val price = data?.price
        val image = data?.image.toString()

        val fromId = FirebaseAuth.getInstance().uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/cart/$fromId/").push()

        ref.get().addOnSuccessListener { snapshot ->
            var quantity = 1
            var total = price!!

            if (snapshot.exists()) {
                val currentProduct = snapshot.getValue(ProductModel::class.java)

                if (currentProduct?.name == name) {
                    Toast.makeText(this, "Produk sudah ada di keranjang", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                quantity = currentProduct?.quantity ?: 1
                quantity++
                total = quantity * price
            }

            val product = ProductModel(ref.key!!, name, desc, owner, image, quantity, price, total)
            ref.setValue(product).addOnSuccessListener {
                Toast.makeText(this, "Berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}