package com.dicoding.tamantic.data.retrofit

import com.dicoding.tamantic.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object{
        fun getApiService(token: String): ApiService {
            val loggingInterceptor = if(BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            val clientBuilder = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
            token.let { authToken ->
                val authInterceptor = Interceptor { chain ->
                    val req = chain.request()
                    val requestHeaders = req.newBuilder()
                        .addHeader("Authorization", "Bearer $authToken")
                        .build()
                    chain.proceed(requestHeaders)
                }
                clientBuilder.addInterceptor(authInterceptor)
            }
            val client = clientBuilder.build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}