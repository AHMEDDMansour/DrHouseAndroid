package com.example.appdrhouseandroid.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http:/192.168.40.19:3000/"

    private val retrofit by lazy {
        // Create logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            // Set level to BODY to see full request and response details
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Add logging interceptor to OkHttpClient
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}