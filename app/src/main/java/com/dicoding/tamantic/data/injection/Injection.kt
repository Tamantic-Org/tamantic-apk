package com.dicoding.tamantic.data.injection

import android.content.Context
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.data.pref.dataStore
import com.dicoding.tamantic.data.repo.UserRepository
import com.dicoding.tamantic.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}