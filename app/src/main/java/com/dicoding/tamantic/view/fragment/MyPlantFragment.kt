package com.dicoding.tamantic.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.adapter.AddressAdapter
import com.dicoding.tamantic.data.adapter.HistoryAdapter
import com.dicoding.tamantic.data.adapter.PlantAdapter
import com.dicoding.tamantic.data.model.Alamat
import com.dicoding.tamantic.data.model.MyPlant
import com.dicoding.tamantic.databinding.FragmentMyPlantBinding
import com.dicoding.tamantic.view.activity.history.HistoryActivity
import com.dicoding.tamantic.view.activity.plant.AddNewPlantActivity
import com.dicoding.tamantic.view.activity.plant.DetailMyPlantActivity
import com.dicoding.tamantic.view.utils.getImageUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class MyPlantFragment : Fragment(R.layout.fragment_my_plant) {

    private lateinit var binding: FragmentMyPlantBinding
    private lateinit var recylerView: RecyclerView
    private lateinit var plantAdapter: PlantAdapter
    private var plantList = mutableListOf<MyPlant>()
    private var lastPlantMap = HashMap<String, MyPlant>()

    private var currentImageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPlantBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recylerView = binding.rvMyplant
        recylerView.layoutManager = LinearLayoutManager(this.context)

        plantAdapter = PlantAdapter(plantList)
        recylerView.adapter = plantAdapter

        setupAction()
        getMyPlant()
    }

    private fun setupAction() {

        binding.actionAddPlant.setOnClickListener { camera() }

        binding.consPlant2.setOnClickListener {
            val intent = Intent(this.context, DetailMyPlantActivity::class.java)
            startActivity(intent)
        }

        binding.history.setOnClickListener {
            val intent = Intent(this.context, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun refreshRv() {
        plantList.clear()
        lastPlantMap.values.forEach {
            plantList.add(it)
            plantAdapter.notifyDataSetChanged()
        }
    }

    private fun getMyPlant() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/my-plant/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val plant = snapshot.getValue(MyPlant::class.java) ?: return
                lastPlantMap[snapshot.key!!] = plant
                refreshRv()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val plant = snapshot.getValue(MyPlant::class.java) ?: return
                lastPlantMap[snapshot.key!!] = plant
                refreshRv()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val plant = snapshot.getValue(MyPlant::class.java) ?: return
                lastPlantMap[snapshot.key!!] = plant
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

    private fun camera() {
        currentImageUri = getImageUri(this.requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            resultImage()
        }
    }

    private fun resultImage() {
        currentImageUri?.let {
            val intent = Intent(this.context, AddNewPlantActivity::class.java).apply {
                putExtra("imageUri", it.toString())
            }
            startActivity(intent)
        }
    }
}