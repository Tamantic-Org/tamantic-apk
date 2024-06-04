package com.dicoding.tamantic.view.activity.chatting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.data.adapter.ChatAdapter
import com.dicoding.tamantic.data.model.Chat
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.databinding.ActivityDetailChatActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChatlogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailChatActivityBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recylerVIew: RecyclerView

    private val messages = mutableListOf<Chat>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getParcelableExtra<UserModel>("USER_KEY")
        val name = user?.name.toString()
        val image = user?.imageUrl.toString()
        val uid = user?.uid.toString()

        getProfile(name, image)
        getMessage(uid)

        recylerVIew = binding.rvChatlog
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        recylerVIew.layoutManager = layoutManager


        chatAdapter = ChatAdapter(messages, FirebaseAuth.getInstance().uid!!)
        recylerVIew.adapter = chatAdapter

        binding.actionBack.setOnClickListener { onBackPressed() }
        binding.actionSend.setOnClickListener { sendMessage(uid) }
    }

    private fun sendMessage(uid: String) {
        val textMessage = binding.inputMessage.text.toString()
        val fromId = FirebaseAuth.getInstance().uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/message/$fromId/$uid").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/message/$uid/$fromId").push()

        val lastMsg = FirebaseDatabase.getInstance().getReference("/last-message/$uid/$fromId")
        val toLastMsg = FirebaseDatabase.getInstance().getReference("/last-message/$fromId/$uid")

        val msg = Chat(ref.key!!, fromId, uid, textMessage, System.currentTimeMillis() / 1000)

        ref.setValue(msg).addOnSuccessListener {
            Log.d("CHAT", "Send Message Succsessfully with ${ref.key}")
            binding.rvChatlog.scrollToPosition(chatAdapter.itemCount - 1)
        }

        toRef.setValue(msg)
        lastMsg.setValue(msg)
        toLastMsg.setValue(msg)

        binding.inputMessage.setText("")
    }

    private fun getProfile(name: String, image: String) {
        binding.nameChatlog.text = name
        Glide.with(binding.imageChatlog).load(image).into(binding.imageChatlog)
    }

    private fun getMessage(uid: String) {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/message/$fromid/$uid")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue(Chat::class.java) ?: return
                msg.let {
                    messages.add(it!!)
                    chatAdapter.notifyItemInserted(messages.size - 1)
                }
                chatAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue(Chat::class.java) ?: return
                msg.let {
                    messages.add(it!!)
                    chatAdapter.notifyItemInserted(messages.size - 1)
                }
                chatAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
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