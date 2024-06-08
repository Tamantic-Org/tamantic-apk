package com.dicoding.tamantic.data.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.MyPlant
import com.dicoding.tamantic.view.activity.plant.DetailPlantActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory

class PlantAdapter(private var plantList: MutableList<MyPlant>): RecyclerView
    .Adapter<PlantAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.findViewById<ImageView>(R.id.image_myplant)
        val tvDesc = view.findViewById<TextView>(R.id.title_myplant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_my_plant,
            parent ,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = plantList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = plantList[position]

        holder.tvDesc.text = data.description
        Glide.with(holder.ivImage).load(data.image).into(holder.ivImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailPlantActivity::class.java)
            intent.putExtra("MY_PLANT", data)
            holder.itemView.context.startActivity(intent)
        }
    }
}