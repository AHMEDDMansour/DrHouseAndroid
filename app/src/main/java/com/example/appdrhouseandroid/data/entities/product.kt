package com.example.appdrhouseandroid.data.entities

import com.example.appdrhouseandroid.data.network.ProductResponse

data class Product(
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String? = null
)

