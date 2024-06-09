package com.dicoding.tamantic.data.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.databinding.ItemListFromChatlogBinding
import com.dicoding.tamantic.databinding.ItemListToChatlogBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatAdapter(private val messages: MutableList<Chat>, private val currentuser: String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_FROM = 1
        private const val VIEW_TYPE_TO = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TO) {
            val binding = ItemListToChatlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ToViewHolder(binding)
        } else {
            val binding = ItemListFromChatlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FromViewHolder(binding)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is ToViewHolder) {
            holder.bind(message)
        } else if (holder is FromViewHolder) {
            holder.bind(message)
        }
    }

    inner class FromViewHolder(private val binding: ItemListFromChatlogBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(chat: Chat) {
            binding.messageChatFrom.text = chat.text
            binding.chatTimeFrom.text = formatTimestamp(chat.timeStamp)
        }
    }

    inner class ToViewHolder(private val binding: ItemListToChatlogBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(chat: Chat) {
            binding.messageChatTo.text = chat.text
            binding.chatTimeTo.text = formatTimestamp(chat.timeStamp)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp)
        val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.fromId == currentuser) VIEW_TYPE_TO else VIEW_TYPE_FROM
    }

    override fun getItemCount(): Int = messages.size
}
