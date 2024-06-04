package com.dicoding.tamantic.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.data.pref.dataStore
import com.dicoding.tamantic.databinding.FragmentProfileBinding
import com.dicoding.tamantic.view.activity.maps.LocationActivity
import com.dicoding.tamantic.view.starter.login.LoginActivity
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

        binding.actionBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        getProfile()

        binding.logoutBtn.setOnClickListener{ logout() }
        binding.messageProfile.setOnClickListener{
//            findNavController().navigate(R.id.action_profileFragment_to_chatFragment)
        }
        binding.locationProfile.setOnClickListener {
            val intent = Intent(this.context, LocationActivity::class.java)
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
    }

    private fun logout() {
        lifecycleScope.launch {
            context?.let { UserPreference.getInstance(it.dataStore).logout() }
            mAuth.signOut()
            mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                val intent = Intent(this@ProfileFragment.context, LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            Log.d("TAG", context?.let { UserPreference.getInstance(it.dataStore).getSession().firstOrNull() }.toString())
        }
    }

}