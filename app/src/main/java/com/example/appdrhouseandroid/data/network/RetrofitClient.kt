package com.example.appdrhouseandroid.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Change the BASE_URL to your backend address. You can update it later based on the environment (local or production).
     const val BASE_URL = "http:/192.168.100.12:3000/"

    private val retrofit by lazy {
        // Add logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Add the interceptor
            .build()

        val gson = GsonBuilder().setLenient().create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Function to get the API service
    fun getApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
