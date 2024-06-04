package com.dicoding.tamantic.data.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.view.activity.chatting.ChatlogActivity
import com.dicoding.tamantic.view.activity.shopping.DetailProductActivity
import com.dicoding.tamantic.view.viewModel.MarketViewModel
import java.text.NumberFormat
import java.util.Locale

class MarketAdapter(var dataProduct: List<DataItem>) : RecyclerView.Adapter<MarketAdapter
    .MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName : TextView = view.findViewById(R.id.name_product_market)
        val tvDesc : TextView = view.findViewById(R.id.deskripsi_product_market)
        val tvPrice : TextView = view.findViewById(R.id.price_product_market)
        val tvRate : TextView = view.findViewById(R.id.rating_product_market)
        val tvSold : TextView = view.findViewById(R.id.sold_product_market)
        val ivImage : ImageView = view.findViewById(R.id.image_product_market)
        val cardView : CardView = view.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_product_toko,
            parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = dataProduct.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataProduct[position]

        holder.tvName.text = data.name
        holder.tvDesc.text = data.description
        holder.tvRate.text = data.rate.toString()
        holder.tvSold.text = "${data.sold} terjual"

        val formattedTotalPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(data.price)
        holder.tvPrice.text = formattedTotalPrice.toString()

        Glide.with(holder.ivImage).load(data.image).into(holder.ivImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailProductActivity::class.java)
            intent.putExtra("PRODUCT_KEY", data)
            holder.itemView.context.startActivity(intent)
        }
    }
}