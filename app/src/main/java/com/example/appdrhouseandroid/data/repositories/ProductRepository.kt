package com.example.appdrhouseandroid.data.repositories



import android.util.Log
import com.example.appdrhouseandroid.data.network.ApiService
import com.example.appdrhouseandroid.data.network.ProductRequest
import com.example.appdrhouseandroid.data.network.ProductResponse
import com.example.appdrhouseandroid.ui.theme.product.OrderRequest
import com.example.appdrhouseandroid.ui.theme.product.OrderResponse
import com.google.gson.Gson
import okhttp3.MultipartBody
import retrofit2.Response


class ProductRepository(private val apiService: ApiService) {

    // Get all products
    suspend fun getAllProducts(): Response<List<ProductResponse>> {
        return apiService.getAllProducts()
    }


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

    // In ProductRepository.kt
    suspend fun createOrder(orderRequest: OrderRequest): Response<OrderResponse> {
        Log.d("ProductRepository", "Creating order with payload: ${Gson().toJson(orderRequest)}")
        return try {
            val response = apiService.createOrder(orderRequest)
            if (!response.isSuccessful) {
                Log.e("ProductRepository", "Error response: ${response.code()} - ${response.errorBody()?.string()}")
            }
            response
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error creating order", e)
            throw e
        }
    }

    suspend fun getOrders(): Response<List<OrderResponse>> {
        return try {
            apiService.getOrders()
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error fetching orders", e)
            throw e
        }
    }


}