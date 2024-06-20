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
import com.dicoding.tamantic.data.response.RecomendedResponse
import com.dicoding.tamantic.data.response.RecommendationItem
import com.dicoding.tamantic.data.retrofit.ApiConfig
import com.google.android.play.integrity.internal.q
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _productData = MutableLiveData<List<DataItem>?>()
    val productData: MutableLiveData<List<DataItem>?> = _productData

    private val _recomendedData = MutableLiveData<List<RecommendationItem>?>()
    val recomendedData: MutableLiveData<List<RecommendationItem>?> = _recomendedData

    fun getProduct(token: String) {
        ApiConfig.getApiService(token).getProducts().enqueue(object :
            Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>
            ) {
                val responseData = response.body()
                responseData.let {
                    if (it != null) {
                        _productData.value = it.data as List<DataItem>?
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

    fun getRecomended(name: String?) {
        val query = name ?: "melati"
        ApiConfig.getApiService("").getRecomended(query).enqueue(object :
            Callback<RecomendedResponse> {
            override fun onResponse(
                call: Call<RecomendedResponse>,
                response: Response<RecomendedResponse>
            ) {
                val responseData = response.body()
                val recomended = responseData
                recomended.let {
                    if (it != null) {
                        _recomendedData.value = it.recommendation as List<RecommendationItem>?
                    }
                }

            }

            override fun onFailure(call: Call<RecomendedResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


}