package com.example.appdrhouseandroid.data.network

import com.example.appdrhouseandroid.data.entities.Product
import com.example.appdrhouseandroid.data.entities.user
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val message: String, val resetCode: String)

data class VerifyCodeRequest(val email: String ,val code: String)

data class VerifyCodeResponse(val message: String)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)

data class OcrResponse(
    val text: String
)



data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
 //   val imageLink : String

)

// ProductResponse.kt
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image : String? = null
)

data class ProductCategoryRequest(
    val page: Int = 1,        // Default to page 1
    val sortBy: String = "name"  // Default to sorting by 'name'
)


interface ApiService {
    @POST("auth/signup") // Ensure this matches your backend route
    suspend fun signUp(@Body signupRequest: SignUpRequest): Response<SignUpResponse>
    // Login API
    @POST("auth/login")  // Adjust to match your login endpoint in the backend
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @POST("auth/verify-reset-code")
    fun verifyResetCode(@Body request: VerifyCodeRequest): Call<VerifyCodeResponse>

    @POST("auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<Void>

     /* OCR */
    @Multipart
    @POST("ocr/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<OcrResponse>

    /*  Product */

    // Create a new product (sending ProductRequest, receiving ProductResponse)
    @POST("product")
    suspend fun createProduct(@Body product: ProductRequest): Response<ProductResponse>

    // Get all products (receiving a list of ProductResponse)
    @GET("product")
    suspend fun getAllProducts(): Response<List<ProductResponse>>

    // Get a product by ID (receiving a ProductResponse)
    @GET("product/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<ProductResponse>

    @GET("product/category/{category}")
    suspend fun getProductByCategory(@Path("category") category: String): Response<List<ProductResponse>>

    // Update a product (sending ProductRequest, receiving ProductResponse)
    @PATCH("product/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: ProductRequest): Response<ProductResponse>

    // Delete a product (receiving a Void response)
    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Void>

    @Multipart
    @POST("product/{id}/upload")
    suspend fun uploadProductImage(
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): Response<ProductResponse>




}