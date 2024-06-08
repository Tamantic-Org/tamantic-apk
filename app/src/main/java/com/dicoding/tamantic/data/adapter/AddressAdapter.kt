package com.dicoding.tamantic.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.Alamat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddressAdapter(private val alamatList: MutableList<Alamat>): RecyclerView
.Adapter<AddressAdapter.ViewHolder>(){
    class ViewHolder (view : View): RecyclerView.ViewHolder(view) {
        val tvAddress: TextView = view.findViewById(R.id.alamat)
        val ivDelete: ImageView = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_address , parent ,
            false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = alamatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = alamatList[position]
        holder.tvAddress.text = data.address

        holder.ivDelete.setOnClickListener {
            val fromId = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/address/$fromId/${data.id}")
            ref.removeValue().addOnSuccessListener {
                alamatList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            }
        }

    }

}