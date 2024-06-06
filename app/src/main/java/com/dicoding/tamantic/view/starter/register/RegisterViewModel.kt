package com.dicoding.tamantic.view.starter.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tamantic.data.repo.UserRepository
import com.dicoding.tamantic.data.response.ErrorResponse
import com.dicoding.tamantic.data.retrofit.ApiConfig
import com.dicoding.tamantic.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.UUID

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _registerError = MutableLiveData<String>()
    val registerError: LiveData<String> = _registerError

    fun registerUser(name: String, email: String, phone: String, password: String, image: Uri?) {
        if (image == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val storageReference = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}.jpg")
                storageReference.putFile(image).await()
                val imageUrl = storageReference.downloadUrl.await().toString()

                val response = ApiConfig.getApiService("").register(name, email, phone, password, imageUrl)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                val user = it.user

                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(data?.name)
                                    .setPhotoUri(Uri.parse(imageUrl))
                                    .build()

                                user?.updateProfile(profileUpdates)
                            }

                        _registerSuccess.postValue(true)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java).message
                        _registerError.postValue(errorMessage)
                    }
                }
            } catch (e: HttpException) {
                val errorMessage = try {
                    val jsonString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonString, ErrorResponse::class.java)
                    errorBody?.message ?: e.message()
                } catch (ex: Exception) {
                    e.message()
                }
                _registerError.postValue(errorMessage)
            } catch (e: Exception) {
                _registerError.postValue(e.message)
            }
        }
    }

    fun playAnimation(binding: ActivityRegisterBinding) {
        val logo = ObjectAnimator.ofFloat(binding.register1, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.register2, View.ALPHA, 1f).setDuration(100)

        val textName = ObjectAnimator.ofFloat(binding.textName, View.ALPHA, 1f).setDuration(100)
        val Lname = ObjectAnimator.ofFloat(binding.nameLayout, View.ALPHA, 1f).setDuration(100)
        val Iname = ObjectAnimator.ofFloat(binding.nameInput, View.ALPHA, 1f).setDuration(100)

        val textEmail = ObjectAnimator.ofFloat(binding.textEmail, View.ALPHA, 1f).setDuration(100)
        val Lemail = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(100)
        val Iemail = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 1f).setDuration(100)

        val textPhone = ObjectAnimator.ofFloat(binding.numberPhone, View.ALPHA, 1f).setDuration(100)
        val Lphone = ObjectAnimator.ofFloat(binding.phoneLayout, View.ALPHA, 1f).setDuration(100)
        val Iphone = ObjectAnimator.ofFloat(binding.phoneInput, View.ALPHA, 1f).setDuration(100)

        val textPass = ObjectAnimator.ofFloat(binding.textPassword, View.ALPHA, 1f).setDuration(100)
        val Lpass = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(100)
        val Ipass = ObjectAnimator.ofFloat(binding.passwordInput, View.ALPHA, 1f).setDuration(100)

        val button = ObjectAnimator.ofFloat(binding.actionRegisterBtn, View.ALPHA, 1f).setDuration(100)
        val aLogin = ObjectAnimator.ofFloat(binding.haveAccount, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                logo,
                title,
                textName,
                Lname,
                Iname,
                textEmail,
                Lemail,
                Iemail,
                textPhone,
                Lphone,
                Iphone,
                textPass,
                Lpass,
                Ipass,
                button,
                aLogin
            )
            start()
        }
    }
}
