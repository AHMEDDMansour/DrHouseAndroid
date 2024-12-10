// ProductViewModel.kt
package com.example.appdrhouseandroid.ui.theme.product

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdrhouseandroid.data.repositories.ProductRepository
import com.example.appdrhouseandroid.data.network.ProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
    val products: StateFlow<List<ProductResponse>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error



    // Load all products
    fun loadAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productRepository.getAllProducts()
            handleResponse(response)
        }
    }

    // Load a product by ID
    fun loadProductById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productRepository.getProductById(id)
            handleResponse(response)
        }
    }


    // Load products by category
    fun loadByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productRepository.getProductByCategory(category)
            handleResponse(response)
        }
    }

    // Handle the API response and update the state
    private fun handleResponse(response: Response<out Any>) {
        if (response.isSuccessful) {
            when (response.body()) {
                is List<*> -> _products.value = response.body() as List<ProductResponse>
                //Log.d("DEBUG_TAG", "TESTING: " + _products.value.le)
                is ProductResponse -> _products.value = listOf(response.body() as ProductResponse)
            }
            _error.value = null
        } else {
            _error.value = response.message()
        }
        _isLoading.value = false
    }
}
