package com.example.appdrhouseandroid.remote

data class SignUpRequest(
    val name : String,
    val email: String,
    val password: String
)

data class SignUpResponse(
    val success: Boolean,
    val message: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)