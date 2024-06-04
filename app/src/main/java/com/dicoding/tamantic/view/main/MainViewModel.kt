package com.dicoding.tamantic.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tamantic.data.repo.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}