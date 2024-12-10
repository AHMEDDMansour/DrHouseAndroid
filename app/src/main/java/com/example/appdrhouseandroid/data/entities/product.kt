package com.example.appdrhouseandroid.data.entities

data class Product(
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String? = null
)
