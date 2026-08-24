package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Presentation-only payment simulator.
 *
 * This deliberately replaces the Daraja STK flow for the school/demo build.
 * It never contacts Safaricom and never moves real money.
 */
class MpesaPaymentViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")

    var isProcessing by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var paymentStatus by mutableStateOf<String?>(null)
        private set

    fun reset() {
        isProcessing = false
        errorMessage = null
        paymentStatus = null
    }

    fun startPayment(
        phone: String,
        amount: Int,
        purpose: String,
        referenceId: String,
        planId: String? = null,
        planLabel: String? = null,
        planDuration: Int? = null,
        onComplete: (Boolean) -> Unit
    ) {
        if (isProcessing) return
        if (amount < 1) {
            errorMessage = "Invalid payment amount."
            onComplete(false)
            return
        }

        errorMessage = null
        paymentStatus = "pending"
        isProcessing = true

        viewModelScope.launch {
            try {
                // Keep the small delay so the presentation visibly demonstrates a payment step.
                delay(900)
                // The actual success/cancel action is exposed separately by the screen.
                onComplete(false)
            } finally {
                isProcessing = false
            }
        }
    }

    /** Complete a demo payment only after the presenter explicitly chooses success. */
    fun simulateSuccessfulPayment(
        amount: Int,
        purpose: String,
        referenceId: String,
        planId: String? = null,
        planLabel: String? = null,
        planDuration: Int? = null,
        onComplete: (Boolean) -> Unit
    ) {
        if (isProcessing) return
        val uid = auth.currentUser?.uid
        
        // During registration, the user doesn't exist yet.
        if (uid == null && purpose != "membership_registration") {
            errorMessage = "Please sign in before completing the demo payment."
            onComplete(false)
            return
        }

        isProcessing = true
        errorMessage = null
        paymentStatus = "pending"

        viewModelScope.launch {
            try {
                delay(650)
                val paymentId = "DEMO-${UUID.randomUUID().toString().take(8).uppercase()}"
                val now = System.currentTimeMillis()

                when (purpose) {
                    "membership_registration" -> {
                        // Return success. RegistrationScreen/AppNavHost handles creation.
                        paymentStatus = "paid"
                        onComplete(true)
                    }
                    "membership_renewal" -> {
                        val memberRef = database.reference.child("members").child(uid!!)
                        val snapshot = memberRef.get().await()
                        val existingExpiry = snapshot.child("membershipExpiry").getValue(String::class.java)
                        val calendar = java.util.Calendar.getInstance()
                        val today = calendar.time
                        val existing = existingExpiry?.let {
                            runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it) }.getOrNull()
                        }
                        val renewalBase = if (existing != null && existing.after(today)) existing else today
                        calendar.time = renewalBase
                        calendar.add(java.util.Calendar.MONTH, planDuration ?: 0)
                        val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                        memberRef.updateChildren(
                            mapOf(
                                "planId" to planId,
                                "planLabel" to planLabel,
                                "planPrice" to amount,
                                "planDuration" to planDuration,
                                "membershipStart" to SimpleDateFormat("yyyy-MM-dd", Locale.US).format(renewalBase),
                                "membershipExpiry" to expiry,
                                "paymentStatus" to "paid",
                                "paymentMethod" to "demo_mpesa",
                                "lastPaymentId" to paymentId,
                                "lastPaymentAt" to now,
                                "lastRenewedAt" to now,
                                "demoPayment" to true
                            )
                        ).await()
                        
                        database.reference.child("members").child(uid).child("demoPaymentHistory").child(paymentId).setValue(
                            mapOf(
                                "paymentId" to paymentId,
                                "purpose" to purpose,
                                "referenceId" to referenceId,
                                "amountKsh" to amount,
                                "status" to "paid",
                                "method" to "demo_mpesa",
                                "paidAt" to now,
                                "environment" to "presentation",
                                "receipt" to paymentId
                            )
                        ).await()
                        paymentStatus = "paid"
                        onComplete(true)
                    }
                    "shop_order" -> {
                        val updates = mapOf(
                            "paymentStatus" to "paid",
                            "paidAt" to now,
                            "demoReceipt" to paymentId
                        )
                        database.reference.child("orders").child(referenceId).updateChildren(updates).await()
                        database.reference.child("members").child(uid!!).child("orders").child(referenceId).updateChildren(updates).await()
                        
                        database.reference.child("members").child(uid).child("demoPaymentHistory").child(paymentId).setValue(
                            mapOf(
                                "paymentId" to paymentId,
                                "purpose" to purpose,
                                "referenceId" to referenceId,
                                "amountKsh" to amount,
                                "status" to "paid",
                                "method" to "demo_mpesa",
                                "paidAt" to now,
                                "environment" to "presentation",
                                "receipt" to paymentId
                            )
                        ).await()
                        paymentStatus = "paid"
                        onComplete(true)
                    }
                    else -> {
                        errorMessage = "Unsupported demo payment type."
                        paymentStatus = "failed"
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                paymentStatus = "failed"
                errorMessage = e.message ?: "Demo payment could not be completed."
                onComplete(false)
            } finally {
                isProcessing = false
            }
        }
    }

    fun simulateCancelledPayment(onComplete: (Boolean) -> Unit) {
        if (isProcessing) return
        isProcessing = true
        errorMessage = null
        paymentStatus = "pending"
        viewModelScope.launch {
            delay(450)
            paymentStatus = "cancelled"
            errorMessage = "Payment cancelled — no payment was made."
            isProcessing = false
            onComplete(false)
        }
    }
}
