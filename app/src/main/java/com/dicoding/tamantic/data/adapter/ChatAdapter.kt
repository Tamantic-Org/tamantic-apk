package com.dicoding.tamantic.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.databinding.ItemListFromChatlogBinding
import com.dicoding.tamantic.databinding.ItemListToChatlogBinding


class ChatAdapter(private val messages: MutableList<Chat>, private val currentuser: String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_FROM = 1
        private const val VIEW_TYPE_TO = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TO){
            val binding = ItemListToChatlogBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
            toViewHolder(binding)
        }else{
            val binding = ItemListFromChatlogBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
            fromViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if(holder is toViewHolder){
            holder.bind(message)
        }else if (holder is fromViewHolder){
            holder.bind(message)
        }
    }

    inner class fromViewHolder(private val binding: ItemListFromChatlogBinding) : RecyclerView
        .ViewHolder(binding.root){
        fun bind(chat: Chat){
            binding.messageChatFrom.text = chat.text
        }
    }

    inner class toViewHolder(private val binding: ItemListToChatlogBinding) : RecyclerView.ViewHolder
        (binding
        .root){
        fun bind(chat: Chat){
            binding.messageChatTo.text = chat.text
        }
    }


    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.fromId == currentuser) VIEW_TYPE_TO else VIEW_TYPE_FROM
    }

    override fun getItemCount(): Int = messages.size


}