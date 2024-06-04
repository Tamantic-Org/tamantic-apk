package com.dicoding.tamantic.view.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.tamantic.data.model.UserModel
import com.dicoding.tamantic.data.repo.UserRepository
import com.dicoding.tamantic.data.response.DataItem
import com.dicoding.tamantic.data.response.ProductsResponse
import com.dicoding.tamantic.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel(
    private val repo: UserRepository
): ViewModel() {

    private val _productData = MutableLiveData<List<DataItem>?>()
    val productData: MutableLiveData<List<DataItem>?> = _productData

    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }

    fun getProduct(token: String, category: String) {
        ApiConfig.getApiService(token).getProductCategory(category).enqueue(object :
            Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>
            ) {
                val response = response.body()
                if(response != null){
                    response.let {
                        productData.value = it?.data as List<DataItem>
                    }
                }else{
                    response.let {
                        productData.value = emptyList()
                    }
                }

            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {

            }

        })
    }

}