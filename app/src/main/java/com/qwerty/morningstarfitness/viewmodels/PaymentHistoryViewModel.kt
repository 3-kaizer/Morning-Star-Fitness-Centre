package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.qwerty.morningstarfitness.ui.screens.payment.PaymentHistoryEntry
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentHistoryViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")

    var entries by mutableStateOf<List<PaymentHistoryEntry>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var loadError by mutableStateOf<String?>(null); private set

    fun refresh() {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true; loadError = null
        viewModelScope.launch {
            try {
                val memberSnapshot = database.reference.child("members").child(uid).get().await()
                val memberId = memberSnapshot.child("memberId").getValue(String::class.java).orEmpty()

                val dedicated = database.reference.child("payments").get().await().children
                    .filter { it.child("memberUid").getValue(String::class.java) == uid || (memberId.isNotBlank() && it.child("memberId").getValue(String::class.java) == memberId) }
                    .map { child -> toHistoryItem(child) }

                // Backward compatibility for data created before the dedicated payments node.
                val legacyPayments = memberSnapshot.child("payments").children.map { child -> toHistoryItem(child) }
                val legacyRenewals = memberSnapshot.child("paymentHistory").children.mapNotNull { child ->
                    if (child.child("purpose").getValue(String::class.java) != "membership_renewal") return@mapNotNull null
                    HistoryItem(
                        id = child.child("paymentId").getValue(String::class.java) ?: child.key.orEmpty(),
                        title = "Membership renewal",
                        amount = child.child("amountKsh").getValue(Long::class.java)?.toInt() ?: child.child("amountKsh").getValue(Double::class.java)?.toInt() ?: 0,
                        paidAt = child.child("paidAt").getValue(Long::class.java) ?: 0L
                    )
                }

                entries = (dedicated + legacyPayments + legacyRenewals)
                    .distinctBy { it.id }
                    .sortedByDescending { it.paidAt }
                    .map { it.toEntry() }
            } catch (e: Exception) {
                loadError = e.message ?: "Could not load payment history."
            } finally { isLoading = false }
        }
    }

    private fun toHistoryItem(child: com.google.firebase.database.DataSnapshot): HistoryItem {
        val type = child.child("type").getValue(String::class.java).orEmpty()
        val planLabel = child.child("planName").getValue(String::class.java)
            ?: child.child("planLabel").getValue(String::class.java).orEmpty()
        val title = when {
            type == "membership_renewal" && planLabel.isNotBlank() -> "Membership renewal — $planLabel"
            type == "membership_registration" && planLabel.isNotBlank() -> "Membership registration — $planLabel"
            type == "shop_order" -> "Shop order payment"
            type == "membership_renewal" -> "Membership renewal"
            else -> "Membership registration"
        }
        val amount = child.child("amountKsh").getValue(Long::class.java)?.toInt()
            ?: child.child("amountKsh").getValue(Double::class.java)?.toInt() ?: 0
        val paidAt = child.child("paidAt").getValue(Long::class.java) ?: 0L
        val id = child.child("paymentId").getValue(String::class.java) ?: child.key.orEmpty()
        val method = displayMethod(child.child("method").getValue(String::class.java))
        return HistoryItem(id, title, amount, paidAt, method)
    }

    private fun displayMethod(raw: String?): String = when (raw) {
        "sandbox_demo" -> "M-Pesa (demo)"
        "mpesa_daraja_sandbox" -> "M-Pesa (sandbox)"
        null, "" -> "M-Pesa"
        else -> raw
    }

    private data class HistoryItem(val id: String, val title: String, val amount: Int, val paidAt: Long, val method: String = "M-Pesa") {
        fun toEntry(): PaymentHistoryEntry {
            val date = if (paidAt > 0) SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date(paidAt)) else "Date unavailable"
            return PaymentHistoryEntry(title, amount, date, id, method)
        }
    }
}
