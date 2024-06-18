package com.dicoding.tamantic.view.activity.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.ImagePagerAdapter
import com.dicoding.tamantic.data.adapter.MarketAdapter
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.data.model.ProductModel
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.data.response.DataItemUser
import com.dicoding.tamantic.databinding.ActivityDetailProductBinding
import com.dicoding.tamantic.view.activity.chatting.ChatlogActivity
import com.dicoding.tamantic.view.main.MainActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.viewModel.MarketViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var viewPagerProdImg: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<DataItem>("PRODUCT_KEY")

        viewPagerProdImg = findViewById(R.id.other_image)

        val imageUrls = listOf(
            "https://www.ruparupa.com/blog/wp-content/uploads/2022/02/macam-macam-tanaman-hias-1.jpg",
            "https://res.cloudinary.com/ruparupa-com/image/upload/w_400,h_400/f_auto,q_auto:eco/v1645765851/Products/10463239_1.jpg",
            "https://res.cloudinary.com/ruparupa-com/image/upload/l_2.1:c5f05d:8cb583/w_738,h_738/f_auto,q_auto:eco/v1660112456/Products/10463239_3.jpg"
        )

        val adapter = ImagePagerAdapter(imageUrls)
        viewPagerProdImg.adapter = adapter

        viewPagerProdImg.setPageTransformer { page, position ->
            val scaleFactor = 0.8f + (1 - Math.abs(position)) * 0.2f
            page.scaleY = scaleFactor
        }

        viewPagerProdImg.offscreenPageLimit = 2

        getProductSelected(data)
        setCategoryProduct(data)
        setupAction(data)
        setupView()
    }

    private fun setCategoryProduct(data: DataItem?) {
        val categoryContainer = binding.categoryContainer
        categoryContainer.removeAllViews()

        val categories = data?.categories

        if (!categories.isNullOrEmpty()) {
            for (category in categories) {
                val categoryButton = AppCompatButton(this).apply {
                    text = category
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    background =
                        ContextCompat.getDrawable(context, R.color.black_main)
                    textSize = 8f
                    typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
                    isAllCaps = false
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(50),
                        dpToPx(30)
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

        val formattedTotalPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(price)
        tvPrice.text = formattedTotalPrice

        tvLocation.text = location
        tvDesc.text = desc
    }

    private fun setupAction(data: DataItem?) {
        binding.actionToBack.setOnClickListener { onBackPressed() }
        binding.actionToCart.setOnClickListener { addToCart(data) }
        binding.chatOwner.setOnClickListener {

            val name = data?.owner
            val image = data?.imageMarket
            val intent = Intent(this,ChatlogActivity::class.java)
            intent.putExtra("OWNER_NAME", name)
            intent.putExtra("OWNER_IMAGE", image)
            startActivity(intent)
        }
    }

    private fun addToCart(data: DataItem?) {
        val status = "dikeranjang"
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
                    return@addOnSuccessListener
                }

                quantity = currentProduct?.quantity ?: 1
                quantity++
                total = quantity * price
            }

            val product = ProductModel(ref.key!!, name, desc, owner, image, status, quantity, price, total)
            ref.setValue(product).addOnSuccessListener {
                popupAlertSuccess("Berhasil ditambahkan ke keranjang")
            }

        }

    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun popupAlertSuccess(msg: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            dialog.dismiss()
        }
        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)
        title.text = "Berhasil"
        message.text = msg
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
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        window.statusBarColor = Color.TRANSPARENT
    }
}