package com.dicoding.tamantic.data.repo

import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.data.response.LoginResponse
import com.dicoding.tamantic.data.response.RegisterResponse
import com.dicoding.tamantic.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse {

        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}