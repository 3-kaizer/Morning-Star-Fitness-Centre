package com.qwerty.morningstarfitness.models

data class ProductModel(
    val id: String,
    val name: String,
    val priceKsh: Int,
    val category: String,
    val description: String? = null,
    val imageUrl: String? = null
)
