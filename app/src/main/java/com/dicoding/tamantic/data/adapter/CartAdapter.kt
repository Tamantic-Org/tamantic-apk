package com.dicoding.tamantic.data.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val context: Context,
    private val productList: MutableList<ProductModel>,
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.image_product_cart)
        val tvName: TextView = view.findViewById(R.id.name_product_cart)
        val tvDesc: TextView = view.findViewById(R.id.deskripsi_product_cart)
        val tvPrice: TextView = view.findViewById(R.id.price_product_cart)
        val tvQuantity: TextView = view.findViewById(R.id.count_product_cart)

        val actionPlus: ImageView = view.findViewById(R.id.action_to_plus)
        val actionMinus: ImageView = view.findViewById(R.id.action_to_minus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_list_cart, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        var quantity = product.quantity

        if (quantity < 1) {
            holder.actionMinus.visibility = View.GONE
        } else {
            holder.actionMinus.visibility = View.VISIBLE
        }

        holder.actionPlus.setOnClickListener {
            quantity += 1
            holder.tvQuantity.text = quantity.toString()
            updateQuantity(holder, product, quantity, position)
        }

        holder.actionMinus.setOnClickListener {
            if (quantity > 0) {
                quantity -= 1
                holder.tvQuantity.text = quantity.toString()
                updateQuantity(holder, product, quantity, position)
            }
        }

        holder.tvName.text = product.name
        holder.tvDesc.text = product.desc

        val formattedTotalPrice =
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(product.total)
        holder.tvPrice.text = formattedTotalPrice
        holder.tvQuantity.text = quantity.toString()

        val imageUri = Uri.parse(product.imageUrl)
        Glide.with(holder.ivImage).load(imageUri).into(holder.ivImage)

    }

    private fun updateQuantity(
        holder: ViewHolder,
        product: ProductModel,
        quantity: Int,
        position: Int
    ) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/cart/$fromId/${product.uid}")

        if (quantity == 0) {
            popupAlert(ref, position, holder, product)
        } else {
            val updates = mapOf("quantity" to quantity)
            ref.updateChildren(updates)
            updatePrice(holder, product, quantity)
        }
    }

    private fun updatePrice(holder: ViewHolder, product: ProductModel, quantity: Int) {
        val totalPrice = quantity * product.price
        val formattedTotalPrice =
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice)
        holder.tvPrice.text = formattedTotalPrice.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/cart/$fromId/${product.uid}")
        ref.child("total").setValue(totalPrice)
    }

    override fun getItemCount(): Int = productList.size

    private fun popupAlert(
        ref: DatabaseReference,
        position: Int,
        holder: ViewHolder,
        product: ProductModel
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.element_popup_alert_2, null)
        val dialog = Dialog(context)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnNo = dialogView.findViewById<Button>(R.id.alert_no)
        btnNo.setOnClickListener {
            dialog.dismiss()
            resetQuantity(holder, product, position)
        }

        val btnYes = dialogView.findViewById<Button>(R.id.alert_yes)
        btnYes.setOnClickListener {
            ref.removeValue()
            productList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
            dialog.dismiss()
        }

        val message = dialogView.findViewById<TextView>(R.id.alert_message)
        val title = dialogView.findViewById<TextView>(R.id.alert_title)

        title.text = "Hapus"
        message.text = "Apa kamu yakin ingin hapus dari keranjang?"

    }

    private fun resetQuantity(holder: ViewHolder, product: ProductModel, position: Int) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/cart/$fromId/${product.uid}")
        val quantity = 1

        val updates = mapOf("quantity" to quantity)
        ref.updateChildren(updates).addOnSuccessListener {
            product.quantity = quantity
            notifyItemChanged(position)
        }

        updatePrice(holder, product, quantity)
    }
}