package com.dicoding.tamantic.view.activity.history

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.HistoryAdapter
import com.dicoding.tamantic.data.model.ScanModel
import com.dicoding.tamantic.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private lateinit var recylerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private var historyList = mutableListOf<ScanModel>()
    private var lastHistoryMap = HashMap<String, ScanModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recylerView = binding.rvHistory
        recylerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = HistoryAdapter(historyList)
        recylerView.adapter = historyAdapter

        getHistory()
        setupAction()
        setupView()
    }

    private fun refreshRv() {
        historyList.clear()
        lastHistoryMap.values.forEach {
            historyList.add(it)
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun getHistory() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/history-scan/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val history = snapshot.getValue(ScanModel::class.java) ?: return
                lastHistoryMap[snapshot.key!!] = history
                refreshRv()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val history = snapshot.getValue(ScanModel::class.java) ?: return
                lastHistoryMap[snapshot.key!!] = history
                refreshRv()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val history = snapshot.getValue(ScanModel::class.java) ?: return
                lastHistoryMap[snapshot.key!!] = history
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

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val statusBarColor = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(this, android.R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> ContextCompat.getColor(this, R.color.white)
            else -> ContextCompat.getColor(this, R.color.white)
        }

        window.statusBarColor = statusBarColor
    }

    private fun setupAction() {
        binding.actionBack.setOnClickListener { onBackPressed() }
    }
}