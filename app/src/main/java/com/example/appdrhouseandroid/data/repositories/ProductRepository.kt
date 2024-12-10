package com.example.appdrhouseandroid.data.repositories

import com.example.appdrhouseandroid.data.network.ApiService
import com.example.appdrhouseandroid.data.network.ProductCategoryRequest
import com.example.appdrhouseandroid.data.network.ProductRequest
import com.example.appdrhouseandroid.data.network.ProductResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response


class ProductRepository(private val apiService: ApiService) {

    // Get all products
    suspend fun getAllProducts(): Response<List<ProductResponse>> {
        return apiService.getAllProducts()
    }

//    suspend fun getProductByCategory(category: String): List<ProductResponse> {
//        // Call the API with both category and productRequest
//        val response = apiService.getProductByCategory(category)
//
//        if (response.isSuccessful) {
//            return response.body() ?: emptyList()
//        } else {
//            throw Exception("Error fetching products by category: ${response.message()}")
//        }
//    }
suspend fun getProductByCategory(category: String): Response<List<ProductResponse>> {
    return apiService.getProductByCategory(category)
}
    suspend fun getProductById(id: String): Response<ProductResponse> {
        return apiService.getProduct(id)
    }
    suspend fun getProduct(productId: String): Response<ProductResponse> {
        return apiService.getProduct(productId)
    }

    // Create a new product
    suspend fun createProduct(productRequest: ProductRequest): Response<ProductResponse> {
        return apiService.createProduct(productRequest)
    }

    // Update a product
    suspend fun updateProduct(id: String, productRequest: ProductRequest): Response<ProductResponse> {
        return apiService.updateProduct(id, productRequest)
    }

    // Delete a product
    suspend fun deleteProduct(id: String): Response<Void> {
        return apiService.deleteProduct(id)
    }

    // Upload an image for a product
    suspend fun uploadProductImage(productId: String, imageFile: MultipartBody.Part): Response<ProductResponse> {
        return apiService.uploadProductImage(productId, imageFile)
    }
}