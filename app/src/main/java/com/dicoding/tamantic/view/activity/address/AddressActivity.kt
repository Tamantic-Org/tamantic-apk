package com.dicoding.tamantic.view.activity.address

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.AddressAdapter
import com.dicoding.tamantic.data.model.Alamat
import com.dicoding.tamantic.databinding.ActivityAddressBinding
import com.dicoding.tamantic.view.activity.maps.LocationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var addressAdapter: AddressAdapter
    private var alamatList = mutableListOf<Alamat>()
    private var lastAddressMap = HashMap<String, Alamat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvAddress
        recyclerView.layoutManager = LinearLayoutManager(this)

        addressAdapter = AddressAdapter(alamatList)
        recyclerView.adapter = addressAdapter

        getAddress()
        setupAction()
        setupView()
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

        binding.actionToMaps.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
    }


    private fun refreshRv() {
        alamatList.clear()
        lastAddressMap.values.forEach {
            alamatList.add(it)
            addressAdapter.notifyDataSetChanged()
        }
    }

    private fun getAddress() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/address/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val address = snapshot.getValue(Alamat::class.java) ?: return
                lastAddressMap[snapshot.key!!] = address
                refreshRv()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val address = snapshot.getValue(Alamat::class.java) ?: return
                lastAddressMap[snapshot.key!!] = address
                refreshRv()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val alamat = snapshot.getValue(Alamat::class.java) ?: return
                lastAddressMap[snapshot.key!!] = alamat
                refreshRv()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
