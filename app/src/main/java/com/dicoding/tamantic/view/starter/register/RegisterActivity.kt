package com.dicoding.tamantic.view.starter.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.response.RegisterResponse
import com.dicoding.tamantic.data.retrofit.ApiConfig
import com.dicoding.tamantic.databinding.ActivityRegisterBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.starter.login.LoginActivity
import com.dicoding.tamantic.view.starter.login.LoginViewModel
import com.dicoding.tamantic.view.utils.createCustomTempFile
import com.google.android.gms.fido.u2f.api.common.RegisterRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var alertDialog: AlertDialog

//    private val viewModel by viewModels<RegisterViewModel> {
//        ViewModelFactory.getInstance(this)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        playAnimation(viewModel)
        setupView()
        setupAction()

    }

    private val PICK_IMAGE_REQUEST = 1
    private var image: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            image = data?.data
            binding.selectUserImage?.setImageURI(image)
            binding.icGaleri?.visibility = View.GONE
        }
    }

    private fun setupAction() {
        binding.selectUserImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.actionRegisterBtn.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val phone = "+62" + binding.phoneInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (name.isNotEmpty() &&
                email.isNotEmpty() &&
                phone.isNotEmpty() &&
                password.isNotEmpty()
            ) {
                registerUser(name, email, phone, password, image)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.actionToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.actionBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        image: Uri?
    ) {
        if (image == null) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storageReference =
                    FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}.jpg")
                storageReference.putFile(image).await()
                val imageUrl = storageReference.downloadUrl.await().toString()


                val response =
                    ApiConfig.getApiService("").register(
                        name,
                        email,
                        phone,
                        password,
                        imageUrl,
                    )


                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        //simpan ke realtimedatabase with email auth
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {

                                val user = it.user

                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(data?.name)
                                    .setPhotoUri(Uri.parse(imageUrl))
                                    .build()

                                user?.updateProfile(profileUpdates)
                            }

                        showLoginSuccessDialog(data?.name!!)

                    } else {
                        Log.d("data", "Error: ${response.errorBody()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("data", "Exception: ${e.message}")
            }

        }
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
        supportActionBar?.hide()
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun playAnimation(viewModel: RegisterViewModel) {
        viewModel.animation(binding)
    }

    private fun showLoginSuccessDialog(name: String) {
        alertDialog = AlertDialog.Builder(this)
            .setTitle("Register Success!")
            .setMessage(
                "${name} akunmu sudah jadi nih. Yuk, login dan mulai" +
                        " " +
                        "berbelanja."
            )
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }.create()
        alertDialog.show()
    }

}