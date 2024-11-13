package com.example.appdrhouseandroid.data.network

import com.example.appdrhouseandroid.data.entities.user
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


// Request model for login
data class LoginRequest(
    val email: String,
    val password: String
)

// Response model for login (adjust to match your backend response)
data class LoginResponse(
    val accessToken: String,  // JWT token returned from the server
    val refreshToken: String,  // Refresh token returned from the server
    val userId: String         // User ID returned from the server
)

data class SignUpRequest(
    val name : String,
    val email: String,
    val password: String
)

data class SignUpResponse(
    val success: Boolean,
    val message: String
)




interface ApiService {
    @POST("auth/signup") // Ensure this matches your backend route
    suspend fun signUp(@Body signupRequest: SignUpRequest): Response<SignUpResponse>
    // Login API
    @POST("auth/login")  // Adjust to match your login endpoint in the backend
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}