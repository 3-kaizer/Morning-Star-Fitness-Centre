package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.qwerty.morningstarfitness.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MpesaPaymentViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")
    private val http = OkHttpClient.Builder().callTimeout(25, TimeUnit.SECONDS).build()
    private val gson = Gson()

    var isProcessing by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    var paymentStatus by mutableStateOf<String?>(null); private set
    var mpesaReceipt by mutableStateOf<String?>(null); private set

    fun reset() { isProcessing = false; errorMessage = null; paymentStatus = null; mpesaReceipt = null }

    fun startStkPayment(phone: String, amount: Int, purpose: String, referenceId: String, planId: String? = null, planLabel: String? = null, planDuration: Int? = null, onComplete: (Boolean) -> Unit) {
        if (isProcessing) return
        if (amount < 1) { errorMessage = "Invalid payment amount."; onComplete(false); return }
        if (phone.isBlank()) { errorMessage = "A valid M-Pesa phone number is required."; onComplete(false); return }
        errorMessage = null; paymentStatus = "starting"; mpesaReceipt = null; isProcessing = true
        viewModelScope.launch {
            try {
                val body = gson.toJson(mapOf("phone" to phone, "amount" to amount, "purpose" to purpose, "referenceId" to referenceId))
                val request = Request.Builder().url(BuildConfig.MPESA_SERVER_URL + "mpesa/stkpush").post(body.toRequestBody("application/json".toMediaType())).build()
                val response = http.newCall(request).execute()
                val responseText = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val message = runCatching { gson.fromJson(responseText, JsonObject::class.java).get("error")?.asString }.getOrNull()
                    throw Exception(message ?: "M-Pesa server rejected the request.")
                }
                val accepted = gson.fromJson(responseText, JsonObject::class.java).get("accepted")?.asBoolean == true
                if (!accepted) throw Exception("M-Pesa STK Push was not accepted.")
                paymentStatus = "pending"
                if (!waitForVerifiedPayment(referenceId)) { onComplete(false); return@launch }

                when (purpose) {
                    "membership_registration" -> { paymentStatus = "paid"; onComplete(true) }
                    "membership_renewal" -> {
                        val uid = auth.currentUser?.uid ?: throw Exception("Please sign in before renewing membership.")
                        val memberRef = database.reference.child("members").child(uid)
                        val snapshot = memberRef.get().await()
                        if (!snapshot.exists()) throw Exception("Member profile not found.")
                        val existingExpiry = snapshot.child("membershipExpiry").getValue(String::class.java)
                        val calendar = Calendar.getInstance(); val today = calendar.time
                        val existing = existingExpiry?.let { runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it) }.getOrNull() }
                        val renewalBase = if (existing != null && existing.after(today)) existing else today
                        calendar.time = renewalBase; calendar.add(Calendar.MONTH, planDuration ?: 0)
                        val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                        val now = System.currentTimeMillis(); val receipt = mpesaReceipt ?: referenceId
                        memberRef.updateChildren(mapOf("planId" to planId, "planLabel" to planLabel, "planPrice" to amount, "planDuration" to planDuration, "membershipStart" to SimpleDateFormat("yyyy-MM-dd", Locale.US).format(renewalBase), "membershipExpiry" to expiry, "paymentStatus" to "paid", "paymentMethod" to "mpesa_daraja_sandbox", "lastPaymentId" to receipt, "lastPaymentAt" to now, "lastRenewedAt" to now)).await()
                        database.reference.child("members").child(uid).child("paymentHistory").child(referenceId).setValue(mapOf("paymentId" to receipt, "purpose" to purpose, "referenceId" to referenceId, "amountKsh" to amount, "status" to "paid", "method" to "mpesa_daraja_sandbox", "paidAt" to now, "environment" to "sandbox", "receipt" to receipt)).await()
                        paymentStatus = "paid"; onComplete(true)
                    }
                    "shop_order" -> {
                        val uid = auth.currentUser?.uid ?: throw Exception("Please sign in before paying for an order.")
                        val receipt = mpesaReceipt ?: referenceId; val now = System.currentTimeMillis()
                        val updates = mapOf("paymentStatus" to "paid", "paymentMethod" to "mpesa_daraja_sandbox", "paidAt" to now, "mpesaReceipt" to receipt, "status" to "pending_pickup")
                        val orderRef = database.reference.child("orders").child(referenceId); val orderSnapshot = orderRef.get().await()
                        if (!orderSnapshot.exists()) throw Exception("Order could not be found.")
                        if (orderSnapshot.child("userId").getValue(String::class.java) != uid) throw Exception("This order does not belong to the signed-in member.")
                        orderRef.updateChildren(updates).await(); database.reference.child("members").child(uid).child("orders").child(referenceId).updateChildren(updates).await()
                        database.reference.child("members").child(uid).child("paymentHistory").child(referenceId).setValue(mapOf("paymentId" to receipt, "purpose" to purpose, "referenceId" to referenceId, "amountKsh" to amount, "status" to "paid", "method" to "mpesa_daraja_sandbox", "paidAt" to now, "environment" to "sandbox", "receipt" to receipt)).await()
                        paymentStatus = "paid"; onComplete(true)
                    }
                    else -> throw Exception("Unsupported payment type.")
                }
            } catch (e: Exception) { paymentStatus = "failed"; errorMessage = e.message ?: "M-Pesa payment failed."; onComplete(false) }
            finally { isProcessing = false }
        }
    }

    private suspend fun waitForVerifiedPayment(referenceId: String): Boolean {
        repeat(30) {
            delay(2000)
            val encoded = URLEncoder.encode(referenceId, "UTF-8")
            val request = Request.Builder().url(BuildConfig.MPESA_SERVER_URL + "mpesa/status/$encoded").get().build()
            try {
                val response = http.newCall(request).execute(); val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) continue
                val json = gson.fromJson(text, JsonObject::class.java); val status = json.get("status")?.asString
                if (status == "paid") { mpesaReceipt = json.get("mpesaReceiptNumber")?.takeIf { !it.isJsonNull }?.asString; paymentStatus = "paid"; return true }
                if (status == "failed") { errorMessage = json.get("resultDesc")?.takeIf { !it.isJsonNull }?.asString ?: "M-Pesa payment was cancelled or failed."; paymentStatus = "failed"; return false }
            } catch (_: Exception) { }
        }
        errorMessage = "M-Pesa confirmation timed out. Check the phone and try again."; paymentStatus = "failed"; return false
    }
}
