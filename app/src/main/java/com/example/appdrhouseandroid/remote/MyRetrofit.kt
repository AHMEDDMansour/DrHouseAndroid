package com.example.appdrhouseandroid.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "http://10.0.2.2:3000/"

abstract class MyRetrofit {

    companion object {

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//        .client(OkHttpClient().newBuilder().addInterceptor())  // pour ajouter un token
                .build()
        }
    }
}
