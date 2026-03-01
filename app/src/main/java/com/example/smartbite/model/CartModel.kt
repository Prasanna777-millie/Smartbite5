package com.example.smartbite.model

data class CartModel(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val imageRes: Int = 0,
    val quantity: Int = 1
)
