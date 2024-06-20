package com.dicoding.tamantic.data.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.response.RecommendationItem
import com.dicoding.tamantic.view.activity.shopping.DetailProductActivity
import com.dicoding.tamantic.view.activity.shopping.ShoppingActivity
import java.text.NumberFormat
import java.util.Locale

class RecomendedAdapter(var recomendedProduct: List<RecommendationItem>) : RecyclerView
    .Adapter<RecomendedAdapter
    .MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle : TextView = view.findViewById(R.id.title_product)
        val tvPrice : TextView = view.findViewById(R.id.harga_product)
        val ivImage : ImageView = view.findViewById(R.id.image_product)
        val cardView : CardView = view.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recomended_product,
            parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = recomendedProduct.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = recomendedProduct[position]

        if(data.price == null){
            holder.tvPrice.text = "harga tidak diketahui"
        }else{
            val formattedTotalPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(data.price)
            holder.tvPrice.text = formattedTotalPrice.toString()
        }

        holder.tvTitle.text = data.name

        val image = Uri.parse(data.image)
        Glide.with(holder.ivImage).load(image).into(holder.ivImage)

    }


}