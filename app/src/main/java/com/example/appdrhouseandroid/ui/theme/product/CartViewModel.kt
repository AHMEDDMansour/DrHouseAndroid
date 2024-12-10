package com.example.appdrhouseandroid.ui.theme.product

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.appdrhouseandroid.data.network.ProductResponse

class CartViewModel : ViewModel() {
    private val _cartItems =mutableStateListOf<ProductResponse>()
    val cartItems: List<ProductResponse> get() = _cartItems

    // Add a product to the cart
    fun addToCart(product: ProductResponse) {
        _cartItems.add(product)
    }

    // Remove a product from the cart
    fun removeFromCart(product: ProductResponse) {
        _cartItems.remove(product)
    }

    // Calculate the total price of items in the cart
    fun calculateTotalPrice(): Double {
        return _cartItems.sumOf { it.price }
    }
}