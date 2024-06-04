package com.dicoding.tamantic.view.starter.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.data.repo.UserRepository
import com.dicoding.tamantic.data.response.LoginResponse
import com.dicoding.tamantic.data.response.RegisterResponse
import com.dicoding.tamantic.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(
//    private val userRepository: UserRepository,
//    private val userPreference: UserPreference
) : ViewModel() {

//    private val _registerResult = MutableLiveData<RegisterResponse>()
//
//    private val _registerSuccess = MutableLiveData<Boolean>()
//    val registerSuccess: LiveData<Boolean> = _registerSuccess
//
//    private val _registerError = MutableLiveData<String>()
//    val registerError: LiveData<String> = _registerError
//
//    private val _isSignUp = MutableLiveData<Boolean>()

//    fun register(name: String, email: String, phone: String, password: String) {
//        viewModelScope.launch {
//            try {
//                val response = userRepository.register(name,email,phone,password)
//                _registerResult.postValue(response)
//
//                if (response != null) {
//                    response.let { result ->
//                        Log.d("RegisterViewModel", result.toString())
//                        userPreference.saveSession(
//                            UserModel(
//                                result.id,
//                                result.name,
//                                result.email,
//                                "",
//                                result.token,
//                                true
//                            )
//                        )
//                        userPreference.logStatus()
//                        _isSignUp.postValue(true)
//                    }
//                    _registerSuccess.postValue(true)
//                } else {
//
//                }
//            } catch (e: HttpException) {
//                _registerError.postValue(e.message())
//            }
//        }
//    }

    fun animation(binding: ActivityRegisterBinding) {
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

        val button =
            ObjectAnimator.ofFloat(binding.actionRegisterBtn, View.ALPHA, 1f).setDuration(100)
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