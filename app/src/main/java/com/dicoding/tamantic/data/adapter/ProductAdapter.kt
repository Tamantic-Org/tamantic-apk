package com.dicoding.tamantic.data.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.Plant
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.view.activity.shopping.ShoppingActivity
import com.dicoding.tamantic.view.fragment.HomeFragment
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(var dataProduct: List<DataItem>) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle : TextView = view.findViewById(R.id.title_product)
        val tvPrice : TextView = view.findViewById(R.id.harga_product)
        val ivImage : ImageView = view.findViewById(R.id.image_product)
        val cardView : CardView = view.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_best_product,
            parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = dataProduct.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataProduct[position]

        if(data.price == null){
            holder.tvPrice.text = "harga tidak diketahui"
        }else{
            val formattedTotalPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(data.price)
            holder.tvPrice.text = formattedTotalPrice.toString()
        }

        holder.tvTitle.text = data.name

        val image = Uri.parse(data.image)
        Glide.with(holder.ivImage).load(image).into(holder.ivImage)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ShoppingActivity::class.java)
            intent.putExtra("PRODUCT_KEY", data)
            context.startActivity(intent)
        }
    }


}