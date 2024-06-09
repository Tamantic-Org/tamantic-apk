package com.dicoding.tamantic.view.starter.register

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.tamantic.R
import com.dicoding.tamantic.databinding.ActivityRegisterBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.starter.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

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
        showLoading(false)

        viewModel.registerSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                showLoading(true)
                popupAlertSuccess()
            }
        }

        viewModel.registerError.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                popupAlertFailed(errorMessage)
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
            binding.selectUserImage.setImageURI(image)
            binding.icGaleri.visibility = View.GONE
        }
    }

    private fun setupAction() {
        binding.selectUserImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST)
        }

        binding.actionRegisterBtn.setOnClickListener {
            showLoading(true)
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val phone = "+62" + binding.phoneInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val image = image

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password
                    .isNotEmpty() && image != null
            ) {
                viewModel.registerUser(name, email, phone, password, image)
            } else {
                popupAlertFailed("Masukan data terlebih dahulu!")
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

    private fun popupAlertSuccess() {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)

        title.text = "Berhasil Mendaftar"
        message.text = "Selamat kamu sudah terdaftar silahkan melakukan login untuk masuk kedalam" +
                " aplikasi"

        showLoading(false)
    }

    private fun popupAlertFailed(errorMessage: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            dialog.dismiss()
        }

        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)
        title.text = "Gagal Mendaftar"
        message.text = errorMessage

        showLoading(false)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar?.visibility = View.VISIBLE
        } else {
            binding.progressBar?.visibility = View.GONE
        }
    }
}
