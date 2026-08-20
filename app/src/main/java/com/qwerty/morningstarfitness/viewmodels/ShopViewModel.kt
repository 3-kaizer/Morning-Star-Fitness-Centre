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

    // Checkout State
    var fulfilmentMethod by mutableStateOf("pickup") // pickup, delivery
    var deliveryLocation by mutableStateOf("")
    var deliveryAddress by mutableStateOf("")
    var currentOrderId by mutableStateOf<String?>(null)

    fun updateQuantity(productId: String, quantity: Int) {
        lastOrderSuccess = false
        orderError = null
        if (!products.any { it.id == productId }) return
        val newCart = cartItems.toMutableMap()
        if (quantity <= 0) {
            newCart.remove(productId)
        } else {
            newCart[productId] = quantity.coerceAtMost(20)
        }
        cartItems = newCart
    }

    fun initiateCheckout() {
        if (cartItems.isEmpty()) {
            orderError = "Your cart is empty."
            return
        }
        currentOrderId = "MS-${UUID.randomUUID().toString().take(6).uppercase()}"
        orderError = null
        lastOrderSuccess = false
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

        if (fulfilmentMethod == "delivery" && (deliveryLocation.isBlank() || deliveryAddress.isBlank())) {
            orderError = "Please provide delivery location and address."
            onComplete(false, null)
            return
        }

        if (isProcessingOrder) return
        isProcessingOrder = true
        orderError = null

        viewModelScope.launch {
            try {
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
                val orderId = currentOrderId ?: "MS-${UUID.randomUUID().toString().take(6).uppercase()}"
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
                    fulfilmentMethod = fulfilmentMethod,
                    deliveryLocation = if (fulfilmentMethod == "delivery") deliveryLocation else "",
                    deliveryAddress = if (fulfilmentMethod == "delivery") deliveryAddress else "",
                    orderedAt = Date(now),
                    demoReceipt = ""
                )

                val orderMap = mapOf(
                    "orderId" to order.orderId,
                    "userId" to order.userId,
                    "memberId" to order.memberId,
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
                    "fulfilmentMethod" to order.fulfilmentMethod,
                    "deliveryLocation" to order.deliveryLocation,
                    "deliveryAddress" to order.deliveryAddress,
                    "orderedAt" to now
                )

                database.reference.child("orders").child(orderId).setValue(orderMap).await()
                database.reference.child("members").child(uid).child("orders").child(orderId).setValue(orderMap).await()
                
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
        deliveryLocation = ""
        deliveryAddress = ""
    }

    suspend fun fetchOrder(orderId: String): OrderModel? {
        return try {
            val snapshot = database.reference.child("orders").child(orderId).get().await()
            if (snapshot.exists()) {
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
                    fulfilmentMethod = snapshot.child("fulfilmentMethod").getValue(String::class.java) ?: "pickup",
                    deliveryLocation = snapshot.child("deliveryLocation").getValue(String::class.java) ?: "",
                    deliveryAddress = snapshot.child("deliveryAddress").getValue(String::class.java) ?: "",
                    paidAt = snapshot.child("paidAt").getValue(Long::class.java)
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun resetOrderState() {
        lastOrderSuccess = false
        orderError = null
        currentOrderId = null
    }

    fun getCartTotal(): Int = cartItems.entries.sumOf { (id, qty) -> 
        (products.find { it.id == id }?.priceKsh ?: 0) * qty 
    }
    
    fun getItemCount(): Int = cartItems.values.sum()
}
