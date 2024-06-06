package com.dicoding.tamantic.view.starter.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.tamantic.databinding.ActivityRegisterBinding
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.starter.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var alertDialog: AlertDialog

    private val IMAGE_REQUEST = 1
    private var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.registerSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                successDialog()
            }
        }

        viewModel.registerError.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                errorDialog(errorMessage)
                Log.d("Error", errorMessage)
            }
        }

        setupView()
        setupAction()

        viewModel.playAnimation(binding)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            image = data?.data
            binding.selectUserImage?.setImageURI(image)
            binding.icGaleri?.visibility = View.GONE
        }
    }

    private fun setupAction() {
        binding.selectUserImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST)
        }

        binding.actionRegisterBtn.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val phone = "+62" + binding.phoneInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                viewModel.registerUser(name, email, phone, password, image)
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

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun successDialog() {
        runOnUiThread {
            alertDialog = AlertDialog.Builder(this)
                .setTitle("Register Success!")
                .setMessage("Akunmu sudah siap! Yuk, login dan mulai berbelanja.")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }.create()
            alertDialog.show()
        }
    }

    private fun errorDialog(errorMessage: String) {
        runOnUiThread {
            val errorDialog = AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .create()

            errorDialog.show()
        }
    }
}
