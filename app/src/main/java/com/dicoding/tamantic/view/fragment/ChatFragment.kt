package com.dicoding.tamantic.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.LastChatAdapter
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.databinding.FragmentChatBinding
import com.dicoding.tamantic.view.activity.chatting.SelectChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private lateinit var recylerView: RecyclerView
    private lateinit var lastChatAdapater: LastChatAdapter
    private var userList = mutableListOf<Chat>()
    private var lastMessageMap = HashMap<String, Chat>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recylerView = binding.rvListChat
        recylerView.layoutManager = LinearLayoutManager(this.context)

        lastChatAdapater = LastChatAdapter(userList)
        recylerView.adapter = lastChatAdapater

        lastMessage()

        binding.actionToSelectUser.setOnClickListener{
            val intent = Intent(this.context, SelectChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun refreshRv(){
        userList.clear()
        lastMessageMap.values.forEach{
            userList.add(it)
            lastChatAdapater.notifyDataSetChanged()
        }
    }

    private fun lastMessage(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/last-message/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(Chat::class.java) ?: return
                lastMessageMap[snapshot.key!!] = user
                refreshRv()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(Chat::class.java) ?: return
                lastMessageMap[snapshot.key!!] = user
                refreshRv()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                lastMessageMap.remove(snapshot.key)
                refreshRv()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}