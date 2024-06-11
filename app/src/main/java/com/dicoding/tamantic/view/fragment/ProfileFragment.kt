package com.dicoding.tamantic.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.data.pref.dataStore
import com.dicoding.tamantic.databinding.FragmentProfileBinding
import com.dicoding.tamantic.view.activity.address.AddressActivity
import com.dicoding.tamantic.view.activity.flowProduct.PackedActivity
import com.dicoding.tamantic.view.activity.maps.LocationActivity
import com.dicoding.tamantic.view.starter.ViewModelFactory
import com.dicoding.tamantic.view.starter.login.LoginActivity
import com.dicoding.tamantic.view.viewModel.ThemeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding:  FragmentProfileBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private val viewModel by viewModels<ThemeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        getProfile()
        val switchTheme = binding.switchTheme

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        setupAction()

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupAction() {

        binding.logoutBtn.setOnClickListener{ popupAlert() }

        binding.locationProfile.setOnClickListener {
            val intent = Intent(this.context, LocationActivity::class.java)
            startActivity(intent)
        }
        binding.actionBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.alamatProfile.setOnClickListener {
            val intent = Intent(this.context, AddressActivity::class.java)
            startActivity(intent)
        }

        binding.dikemas.setOnClickListener{
            val intent = Intent(this.context, PackedActivity::class.java)
            startActivity(intent)
        }

    }

    @SuppressLint("CheckResult")
    private fun getProfile(){
        lifecycleScope.launch {
            val authApi = context?.let { UserPreference.getInstance(it.dataStore).getSession().firstOrNull() }
            val authToken = authApi?.token

            val user = Firebase.auth.currentUser
            val id = user?.uid.toString()

            if (authToken != null && authApi.isLogin) {
                Log.d("TAG", authApi.toString())
                binding.nameProfile.text = authApi.name
                binding.emailProfile.text = authApi.email

                Glide.with(binding.photoProfile)
                    .load(user?.photoUrl)
                    .error(R.drawable.profile_default)
                    .apply {
                        if (user?.photoUrl == null) {
                            placeholder(R.drawable.profile_default)
                        }
                    }
                    .into(binding.photoProfile)

            } else {
                val ref = FirebaseDatabase.getInstance().getReference("/users/$id")
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(UserModel::class.java)
                        user.let {
                            binding.nameProfile.text = it?.name
                            binding.emailProfile.text = it?.email
                            Glide.with(binding.photoProfile)
                                .load(it?.imageUrl)
                                .fitCenter()
                                .error(R.drawable.profile_default)
                                .into(binding.photoProfile)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
        showLoading(false)
    }

    private fun popupAlert() {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert_2, null)
        val dialog = android.app.Dialog(requireActivity())
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_no = dialogBinding.findViewById<Button>(R.id.alert_no)
        btn_no.setOnClickListener {
            dialog.dismiss()
        }

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            logout()
        }

        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)

        title.text = "Keluar"
        message.text = "Apa kamu yakin ingin keluar sekarang?"

        showLoading(false)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            mAuth.signOut()
            context?.let { UserPreference.getInstance(it.dataStore).logout() }
            mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                val intent = Intent(this@ProfileFragment.context, LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            Log.d("TAG", context?.let { UserPreference.getInstance(it.dataStore).getSession().firstOrNull() }.toString())
        }
    }

}