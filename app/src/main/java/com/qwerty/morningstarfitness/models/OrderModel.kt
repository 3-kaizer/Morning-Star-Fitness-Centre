package com.qwerty.morningstarfitness.models

import java.util.Date

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val priceKsh: Int = 0,
    val quantity: Int = 0
)

data class OrderModel(
    val orderId: String = "",
    val memberId: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Int = 0,
    val status: String = "pending",
    val paymentStatus: String = "unpaid",
    val paymentMethod: String = "demo_mpesa",
    val fulfilmentMethod: String = "pickup",
    val deliveryLocation: String = "",
    val deliveryAddress: String = "",
    val orderedAt: Date = Date(),
    val paidAt: Long? = null,
    val demoReceipt: String = ""
)
