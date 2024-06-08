package com.dicoding.tamantic.data.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class PackedAdapter(
    private val productList: MutableList<ProductModel>,
) :
    RecyclerView.Adapter<PackedAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.image_product_packed)
        val tvName: TextView = view.findViewById(R.id.name_product_packed)
        val tvDesc: TextView = view.findViewById(R.id.deskripsi_product_packed)
        val tvPrice: TextView = view.findViewById(R.id.price_product_packed)
        val tvQuantity: TextView = view.findViewById(R.id.count_product_packed)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_list_packed, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        var quantity = product.quantity

        holder.tvName.text = product.name
        holder.tvDesc.text = product.desc
        holder.status.text = product.status

        val formattedTotalPrice =
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(product.total)
        holder.tvPrice.text = formattedTotalPrice
        holder.tvQuantity.text = quantity.toString() + "x"

        val imageUri = Uri.parse(product.imageUrl)
        Glide.with(holder.ivImage).load(imageUri).into(holder.ivImage)

    }

    override fun getItemCount(): Int = productList.size

}
