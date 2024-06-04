package com.dicoding.tamantic.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.tamantic.R
import com.dicoding.tamantic.databinding.FragmentMyPlantBinding
import com.dicoding.tamantic.view.activity.plant.AddNewPlantActivity
import com.dicoding.tamantic.view.activity.plant.DetailMyPlantActivity
import com.dicoding.tamantic.view.utils.getImageUri

class MyPlantFragment : Fragment(R.layout.fragment_my_plant) {

    private lateinit var binding: FragmentMyPlantBinding

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

        binding.scanNewPlant.setOnClickListener { camera() }

        binding.consPlant2.setOnClickListener {
            val intent = Intent(this.context, DetailMyPlantActivity::class.java)
            startActivity(intent)
        }
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