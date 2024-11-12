package com.example.appdrhouseandroid.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/signup")
    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

}