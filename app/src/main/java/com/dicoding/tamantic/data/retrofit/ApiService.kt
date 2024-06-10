package com.dicoding.tamantic.data.retrofit

import com.dicoding.tamantic.data.response.LoginResponse
import com.dicoding.tamantic.data.response.ProductsResponse
import com.dicoding.tamantic.data.response.RegisterResponse
import com.dicoding.tamantic.data.response.ScanResponse
import com.dicoding.tamantic.data.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("imageUrl") imageUrl: String
    ): Response<RegisterResponse>

    @GET("products")
    fun getProducts(): Call<ProductsResponse>

    @GET("user/owner/product/{owner}")
    fun getProductsMarket(
        @Path("owner") owner: String
    ): Call<ProductsResponse>

    @GET("/category/categories/product/{category}")
    fun getProductCategory(
        @Path("category") category: String
    ): Call<ProductsResponse>

    @GET("/products/search/{name}")
    fun getUser(
        @Path("name") owner: String
    ): Call<ProductsResponse>

    @Multipart
    @POST("/predict")
    fun getDataScan(
        @Part file: MultipartBody.Part
    ): Call<ScanResponse>

}