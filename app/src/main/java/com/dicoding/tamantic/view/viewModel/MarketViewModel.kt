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

class MarketViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _productData = MutableLiveData<List<DataItem>?>()
    val productData: MutableLiveData<List<DataItem>?> = _productData

    fun getProductMarket(token: String, owner: String){
        ApiConfig.getApiService(token).getProductsMarket(owner).enqueue(object :
            Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>
            ) {
                val data = response.body()
                data.let{
                    if(data != null){
                        productData.value = it?.data as List<DataItem>?
                    }
                }
            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }
}