package com.dicoding.tamantic.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.pref.dataStore
import com.dicoding.tamantic.databinding.ActivityIntroductionBinding
import com.dicoding.tamantic.databinding.ActivityMainBinding
import com.dicoding.tamantic.view.activity.scan.DetailScanActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.starter.login.LoginActivity
import com.dicoding.tamantic.view.utils.getImageUri
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAuthAndLogin()

        val navController = findNavController(R.id.hostFragment)

        binding.navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.profileFragment) {
                binding.navigationView.visibility = View.GONE
            } else {
                binding.navigationView.visibility = View.VISIBLE
            }
        }

        binding.scanImage.setOnClickListener { camera() }

    }

//    private fun loginValidation(){
//        val uid = FirebaseAuth.getInstance().uid
//        if(uid == null){
//
//            val intent = Intent(this,LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//    }

    private fun camera() {
        currentImageUri = getImageUri(this)
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
            val intent = Intent(this, DetailScanActivity::class.java).apply {
                putExtra("imageUri", it.toString())
            }
            startActivity(intent)
        }
    }

    private fun checkAuthAndLogin() {
        lifecycleScope.launch {
            val isAuthValid = authCheck()
            val isLoginValid = loginValidation()

            if (isAuthValid || isLoginValid) {
                Log.d("MainActivity", "checkAuthAndLogin: $isAuthValid - $isLoginValid")
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }


    private suspend fun authCheck(): Boolean {
        val token = dataStore.data.map { pref ->
            pref[stringPreferencesKey("token")]
        }.firstOrNull()
        Log.d(this.toString(), "authCheck: $token")
        return !token.isNullOrEmpty()
    }

    private fun loginValidation(): Boolean {
        val uid = FirebaseAuth.getInstance().uid
        return uid != null
    }


}