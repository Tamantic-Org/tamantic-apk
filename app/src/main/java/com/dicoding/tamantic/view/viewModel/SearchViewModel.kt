package com.dicoding.tamantic.view.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.tamantic.data.response.DataItemUser
import com.dicoding.tamantic.data.response.UserResponse
import com.dicoding.tamantic.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {

    private val _nameProduct = MutableLiveData<List<DataItemUser>>()
    val nameProduct: MutableLiveData<List<DataItemUser>> = _nameProduct

    private val _isDataEmpty = MutableLiveData<Boolean>()
    val isDataEmpty: LiveData<Boolean> = _isDataEmpty

    fun setProduct(name: String){
        return fetchProduct(name)
    }

    fun fetchProduct(name: String) {
        ApiConfig.getApiService("").getUser(name).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val responseData = response.body()
                responseData.let {
                    if(responseData != null){
                        _nameProduct.value = it?.data as List<DataItemUser>?
                    }else{
                        _isDataEmpty.value = it?.data.isNullOrEmpty()
                    }


                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("MainViewModel", "Failed to fetch data: ${t.message}")
            }

        })
    }
}