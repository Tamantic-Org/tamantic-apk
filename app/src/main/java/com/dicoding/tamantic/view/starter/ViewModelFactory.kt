package com.dicoding.tamantic.view.starter

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.tamantic.data.injection.Injection
import com.dicoding.tamantic.data.pref.UserPreference
import com.dicoding.tamantic.data.pref.dataStore
import com.dicoding.tamantic.data.repo.UserRepository
import com.dicoding.tamantic.view.main.MainViewModel
import com.dicoding.tamantic.view.starter.login.LoginViewModel
import com.dicoding.tamantic.view.viewModel.CategoryViewModel
import com.dicoding.tamantic.view.viewModel.HomeViewModel
import com.dicoding.tamantic.view.viewModel.MarketViewModel

class ViewModelFactory(private val repository: UserRepository, private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MarketViewModel::class.java) -> {
                MarketViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository, pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context), UserPreference.getInstance(context.dataStore))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}