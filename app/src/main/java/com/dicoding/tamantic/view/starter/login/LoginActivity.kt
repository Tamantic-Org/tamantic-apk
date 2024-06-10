package com.dicoding.tamantic.view.starter.login

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.databinding.ActivityLoginBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.starter.register.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(false)
        viewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                showLoading(true)
                popupAlertSuccess()
            }
        }

        viewModel.loginError.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showLoading(true)
                popupAlertFailed(errorMessage)
            }
        }

        val geo = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        playAnimation()
        setupView()
        setupAction() // login manual

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        googleSignInClient = GoogleSignIn.getClient(this, geo)

        binding.loginWithGoogle.setOnClickListener { googleSignIn() } // login with account google

    }


    private fun googleSignIn() {
        val signInClient = googleSignInClient.signInIntent
        launcher.launch(signInClient)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            intent
            manageResults(task)
        }
    }

    private fun manageResults(task: Task<GoogleSignInAccount>) {
        val account: GoogleSignInAccount? = task.result

        //jika account google berhasil
        if (account !== null) {
            showLoading(true)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener {
                if (task.isSuccessful) {
                    val imageUrl = account.photoUrl.toString()
                    val email = account.email.toString()
                    val name = account.displayName.toString()

                    val currentUser = auth.currentUser
                    currentUser?.let {
                        val uid = it.uid
                        val user = UserModel(uid, name, email, imageUrl, "" , true)

                        // simpan data user ke realtimedatabase
                        database.reference.child("users").child(uid).setValue(user)
                            .addOnCompleteListener { databaseTask ->
                                if (databaseTask.isSuccessful) {
                                    popupAlertSuccess()
                                } else {
                                    popupAlertFailed(databaseTask.exception.toString())
                                }
                            }
                    }
                } else {
                    popupAlertFailed(task.exception.toString())
                }
            }

        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.actionLoginBtn.setOnClickListener {
            showLoading(true)
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                //simpan datauser ke realtime database
                Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    val currentUser = auth.currentUser
                    currentUser?.let {
                        val name = it.displayName.toString()
                        val uid = it.uid
                        val image = it.photoUrl.toString()
                        val user = UserModel(uid, name, email, image, "" , true)
                        database.reference.child("users").child(uid).setValue(user)
                            .addOnSuccessListener {
                            }
                    }
                }

                // login manual firestore
                viewModel.login(email, password)

            } else {
                popupAlertFailed("Masukan data terlebih dahulu!")
            }

        }

        binding.actionToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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

    private fun popupAlertSuccess() {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)
        val name = FirebaseAuth.getInstance().currentUser?.displayName ?: binding.emailInput.text
        title.text = "Berhasil"
        message.text = "Selamat datang kembali $name carilah tanaman kesukaan mu sekarang 😁"

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
        title.text = "Gagal"
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

    private fun playAnimation() {
        viewModel.animation(binding)
    }
}