package com.dicoding.tamantic.data.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.view.activity.chatting.ChatlogActivity

class UserAdapter(private val userList: List<UserModel>): RecyclerView.Adapter<UserAdapter
    .ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.image_user)
        val tvName: TextView = view.findViewById(R.id.name_user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_select_user, parent
            , false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = user.name
        Glide.with(holder.ivImage)
            .load(user.imageUrl)
            .into(holder.ivImage)

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, ChatlogActivity::class.java)
            intent.putExtra("USER_KEY", user)
            holder.itemView.context.startActivity(intent)
        }
    }
}