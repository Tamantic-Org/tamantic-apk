package com.dicoding.tamantic.view.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.data.response.DataItemUser
import com.dicoding.tamantic.data.response.ProductsResponse
import com.dicoding.tamantic.data.response.UserResponse
import com.dicoding.tamantic.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {

    private val _nameProduct = MutableLiveData<List<DataItem>>()
    val nameProduct: MutableLiveData<List<DataItem>> = _nameProduct

    private val _isDataEmpty = MutableLiveData<Boolean>()
    val isDataEmpty: LiveData<Boolean> = _isDataEmpty

    fun setProduct(name: String){
        return fetchProduct(name)
    }

    fun fetchProduct(name: String) {
        ApiConfig.getApiService("").getUser(name).enqueue(object : Callback<ProductsResponse> {
            override fun onResponse(call: Call<ProductsResponse>, response: Response<ProductsResponse>) {
                val responseData = response.body()
                responseData.let {
                    if(responseData != null){
                        _nameProduct.value = it?.data as List<DataItem>?
                    }else{
                        _isDataEmpty.value = it?.data.isNullOrEmpty()
                    }


                }
            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                Log.e("MainViewModel", "Failed to fetch data: ${t.message}")
            }

        })
    }
}