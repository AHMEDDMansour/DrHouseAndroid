package com.example.appdrhouseandroid.ID

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appdrhouseandroid.data.repositories.ProductRepository
import com.example.appdrhouseandroid.ui.theme.product.ProductViewModel

class ProductViewModelFactory(private val productRepository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            ProductViewModel(productRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}