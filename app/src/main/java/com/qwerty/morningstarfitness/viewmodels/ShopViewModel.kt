package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.qwerty.morningstarfitness.models.OrderItem
import com.qwerty.morningstarfitness.models.OrderModel
import com.qwerty.morningstarfitness.models.ProductModel
import com.qwerty.morningstarfitness.models.defaultProducts
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class ShopViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")

    var products by mutableStateOf(defaultProducts); private set
    var cartItems by mutableStateOf<Map<String, Int>>(emptyMap()); private set
    var isProcessingOrder by mutableStateOf(false); private set
    var lastOrderSuccess by mutableStateOf(false); private set
    var orderError by mutableStateOf<String?>(null); private set

    // Gym-shop orders are pickup-only at the front desk.
    var fulfilmentMethod by mutableStateOf("pickup")
    var currentOrderId by mutableStateOf<String?>(null)

    fun updateQuantity(productId: String, quantity: Int) {
        lastOrderSuccess = false
        orderError = null
        if (!products.any { it.id == productId }) return
        val newCart = cartItems.toMutableMap()
        if (quantity <= 0) newCart.remove(productId) else newCart[productId] = quantity.coerceAtMost(20)
        cartItems = newCart
    }

    fun initiateCheckout() {
        if (cartItems.isEmpty()) {
            orderError = "Your cart is empty."
            return
        }
        currentOrderId = generateOrderId()
        fulfilmentMethod = "pickup"
        orderError = null
        lastOrderSuccess = false
    }

    private fun generateOrderId(): String {
        val date = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(Date())
        val suffix = UUID.randomUUID().toString().replace("-", "").take(6).uppercase()
        return "ORD-$date-$suffix"
    }

    fun createPendingOrder(onComplete: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            orderError = "Please log in before placing an order."
            onComplete(false, null)
            return
        }
        if (cartItems.isEmpty()) {
            orderError = "Your cart is empty."
            onComplete(false, null)
            return
        }
        if (isProcessingOrder) return
        isProcessingOrder = true
        orderError = null

        viewModelScope.launch {
            try {
                // Member data lives only under /members/{firebaseUid}.
                // Orders live only under /orders/{orderId}. This keeps the Firebase
                // console clean and makes the two collections easy to demonstrate.
                val memberSnapshot = database.reference.child("members").child(uid).get().await()
                if (!memberSnapshot.exists()) throw Exception("Member profile not found.")

                val memberName = memberSnapshot.child("fullName").getValue(String::class.java) ?: "Unknown Member"
                val memberPhone = memberSnapshot.child("phone").getValue(String::class.java) ?: ""
                val memberEmail = memberSnapshot.child("email").getValue(String::class.java) ?: ""
                val memberIdStr = memberSnapshot.child("memberId").getValue(String::class.java) ?: ""

                val items = cartItems.mapNotNull { (id, qty) ->
                    if (qty <= 0) return@mapNotNull null
                    val product = products.find { it.id == id } ?: return@mapNotNull null
                    OrderItem(id, product.name, product.priceKsh, qty)
                }
                if (items.isEmpty()) throw IllegalStateException("Your cart contains no valid products.")

                val total = items.sumOf { it.priceKsh * it.quantity }
                val orderId = currentOrderId ?: generateOrderId()
                val now = System.currentTimeMillis()
                val order = OrderModel(
                    orderId = orderId,
                    userId = uid,
                    memberId = memberIdStr,
                    customerName = memberName,
                    customerPhone = memberPhone,
                    customerEmail = memberEmail,
                    items = items,
                    totalAmount = total,
                    status = "pending",
                    paymentStatus = "unpaid",
                    paymentMethod = "demo_mpesa",
                    fulfilmentMethod = "pickup",
                    orderedAt = Date(now),
                    demoReceipt = ""
                )

                val orderMap = mapOf(
                    "orderId" to order.orderId,
                    "memberId" to order.memberId,
                    "userId" to order.userId,
                    "customerName" to order.customerName,
                    "customerPhone" to order.customerPhone,
                    "customerEmail" to order.customerEmail,
                    "items" to items.map { mapOf(
                        "productId" to it.productId,
                        "productName" to it.productName,
                        "priceKsh" to it.priceKsh,
                        "quantity" to it.quantity
                    ) },
                    "totalAmount" to order.totalAmount,
                    "status" to order.status,
                    "paymentStatus" to order.paymentStatus,
                    "paymentMethod" to order.paymentMethod,
                    "fulfilmentMethod" to "pickup",
                    "pickupLocation" to "Morning Star Fitness Centre Front Desk",
                    "orderedAt" to now
                )

                // Single source of truth for shop orders: /orders/{orderId}.
                // Member records remain separate under /members/{uid}.
                database.reference.child("orders").child(orderId).setValue(orderMap).await()
                onComplete(true, orderId)
            } catch (e: Exception) {
                orderError = e.message ?: "Could not initiate the order."
                onComplete(false, null)
            } finally {
                isProcessingOrder = false
            }
        }
    }

    fun finalizeOrder() {
        cartItems = emptyMap()
        lastOrderSuccess = true
        currentOrderId = null
        fulfilmentMethod = "pickup"
    }

    suspend fun fetchOrder(orderId: String): OrderModel? {
        return try {
            val snapshot = database.reference.child("orders").child(orderId).get().await()
            if (!snapshot.exists()) return null
            val items = snapshot.child("items").children.map { item ->
                OrderItem(
                    productId = item.child("productId").getValue(String::class.java) ?: "",
                    productName = item.child("productName").getValue(String::class.java) ?: "",
                    priceKsh = item.child("priceKsh").getValue(Long::class.java)?.toInt() ?: 0,
                    quantity = item.child("quantity").getValue(Long::class.java)?.toInt() ?: 0
                )
            }
            OrderModel(
                orderId = snapshot.child("orderId").getValue(String::class.java) ?: "",
                userId = snapshot.child("userId").getValue(String::class.java) ?: "",
                memberId = snapshot.child("memberId").getValue(String::class.java) ?: "",
                customerName = snapshot.child("customerName").getValue(String::class.java) ?: "",
                customerPhone = snapshot.child("customerPhone").getValue(String::class.java) ?: "",
                customerEmail = snapshot.child("customerEmail").getValue(String::class.java) ?: "",
                items = items,
                totalAmount = snapshot.child("totalAmount").getValue(Long::class.java)?.toInt() ?: 0,
                status = snapshot.child("status").getValue(String::class.java) ?: "pending",
                paymentStatus = snapshot.child("paymentStatus").getValue(String::class.java) ?: "unpaid",
                paymentMethod = snapshot.child("paymentMethod").getValue(String::class.java) ?: "demo_mpesa",
                fulfilmentMethod = "pickup",
                orderedAt = Date(snapshot.child("orderedAt").getValue(Long::class.java) ?: System.currentTimeMillis()),
                paidAt = snapshot.child("paidAt").getValue(Long::class.java)
            )
        } catch (_: Exception) {
            null
        }
    }

    fun resetOrderState() {
        lastOrderSuccess = false
        orderError = null
        currentOrderId = null
        fulfilmentMethod = "pickup"
    }

    fun getCartTotal(): Int = cartItems.entries.sumOf { (id, qty) ->
        (products.find { it.id == id }?.priceKsh ?: 0) * qty
    }

    fun getItemCount(): Int = cartItems.values.sum()
}
