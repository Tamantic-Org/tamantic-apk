package com.dicoding.tamantic.view.activity.chatting

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.UserAdapter
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.databinding.ActivitySelectChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SelectChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectChatBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var recylerView: RecyclerView
    private var userList = mutableListOf<UserModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBack.setOnClickListener { onBackPressed() }

        recylerView = binding.rvSelectUser
        recylerView.layoutManager = LinearLayoutManager(this)

        userAdapter = UserAdapter(userList)
        recylerView.adapter = userAdapter

        fecthUser()
        setupView()
    }

    private fun fecthUser() {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                snapshot.children.forEach {
                    val user = it.getValue(UserModel::class.java)
                    user.let {
                        userList.add(it!!)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SelectChatActivity, "$error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        supportActionBar?.hide()

        val statusBarColor = ContextCompat.getColor(this, R.color.green_500)
        window.statusBarColor = statusBarColor
    }
}