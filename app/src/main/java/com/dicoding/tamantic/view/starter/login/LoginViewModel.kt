package com.dicoding.tamantic.view.starter.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.data.repo.UserRepository
import com.dicoding.tamantic.data.response.LoginResponse
import com.dicoding.tamantic.databinding.ActivityLoginBinding
import com.dicoding.tamantic.view.main.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val userRepository: UserRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private val _loginResult = MutableLiveData<LoginResponse>()

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> = _loginError

    private val _isLoggedIn = MutableLiveData<Boolean>()

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {

        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                _loginResult.postValue(response)

                // jika data user ada
                if (response.loginResult != null) {

                    //simpan ke preference
                    response.loginResult.let { result ->
                        Log.d("LoginViewModel", result.toString())
                        userPreference.saveSession(
                            UserModel(
                                result.id,
                                result.name,
                                result.email,
                                "",
                                result.token,
                                true
                            )
                        )
                        userPreference.logStatus()
                        _isLoggedIn.postValue(true)

                    }
                    _loginSuccess.postValue(true)
                } else {
                    _loginError.postValue(response.msg)
                }

            } catch (e: HttpException) {
                _loginError.postValue(e.message())
            }
        }
    }


    fun animation(binding: ActivityLoginBinding) {
        val logo = ObjectAnimator.ofFloat(binding.logoLogin, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.titleLogin, View.ALPHA, 1f).setDuration(100)

        val textEmail = ObjectAnimator.ofFloat(binding.textEmail, View.ALPHA, 1f).setDuration(100)
        val Lemail = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(100)
        val Iemail = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 1f).setDuration(100)

        val textPass = ObjectAnimator.ofFloat(binding.textPassword, View.ALPHA, 1f).setDuration(100)
        val Lpass = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(100)
        val Ipass = ObjectAnimator.ofFloat(binding.passwordInput, View.ALPHA, 1f).setDuration(100)

        val forgot = ObjectAnimator.ofFloat(binding.actionForgotPassword, View.ALPHA, 1f)
            .setDuration(100)
        val button = ObjectAnimator.ofFloat(binding.actionLoginBtn, View.ALPHA, 1f).setDuration(100)
        val aRegister =
            ObjectAnimator.ofFloat(binding.bottomAction, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                logo,
                title,
                textEmail,
                Lemail,
                Iemail,
                textPass,
                Lpass,
                Ipass,
                forgot,
                button,
                aRegister
            )
            start()
        }
    }
}