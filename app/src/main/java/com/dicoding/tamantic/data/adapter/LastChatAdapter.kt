package com.dicoding.tamantic.data.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.view.activity.chatting.ChatlogActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LastChatAdapter(private val userList: MutableList<Chat>): RecyclerView.Adapter<LastChatAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.name_chat)
        val ivImage: ImageView = view.findViewById(R.id.image_chat)
        val tvText: TextView = view.findViewById(R.id.message_chat)
        val tvTime: TextView = view.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.tvText.text = user.text
        holder.tvTime.text = formatTimestamp(user.timeStamp)
        val username: String = if (user.fromId == FirebaseAuth.getInstance().uid) {
            user.toId
        } else {
            user.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$username")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                holder.tvName.text = user?.name
                Glide.with(holder.ivImage).load(user?.imageUrl).into(holder.ivImage)

                holder.itemView.setOnClickListener {
                    val intent = Intent(holder.itemView.context, ChatlogActivity::class.java)
                    intent.putExtra("USER_KEY", user)
                    holder.itemView.context.startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp)
        val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}
