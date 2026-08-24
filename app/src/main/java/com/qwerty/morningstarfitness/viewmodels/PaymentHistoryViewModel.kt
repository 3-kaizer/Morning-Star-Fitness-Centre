package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
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

    fun refresh() {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true
        viewModelScope.launch {
            try {
                val memberRef = database.reference.child("members").child(uid)
                val paymentsSnapshot = memberRef.child("payments").get().await()
                val legacySnapshot = memberRef.child("paymentHistory").get().await()

                val unified = paymentsSnapshot.children.map { child ->
                    val type = child.child("type").getValue(String::class.java).orEmpty()
                    val planLabel = child.child("planLabel").getValue(String::class.java).orEmpty()
                    val title = when {
                        type == "membership_renewal" && planLabel.isNotBlank() -> "Membership renewal — $planLabel"
                        type == "membership_registration" && planLabel.isNotBlank() -> "Membership registration — $planLabel"
                        type == "membership_renewal" -> "Membership renewal"
                        else -> "Membership registration"
                    }
                    val amount = child.child("amountKsh").getValue(Long::class.java)?.toInt() ?: child.child("amountKsh").getValue(Double::class.java)?.toInt() ?: 0
                    val paidAt = child.child("paidAt").getValue(Long::class.java) ?: 0L
                    val id = child.child("paymentId").getValue(String::class.java) ?: child.key.orEmpty()
                    HistoryItem(id, title, amount, paidAt)
                }

                // Older app versions wrote renewals to paymentHistory. Keep them visible during migration.
                val legacy = legacySnapshot.children.mapNotNull { child ->
                    val purpose = child.child("purpose").getValue(String::class.java).orEmpty()
                    if (purpose != "membership_renewal") return@mapNotNull null
                    val amount = child.child("amountKsh").getValue(Long::class.java)?.toInt() ?: child.child("amountKsh").getValue(Double::class.java)?.toInt() ?: 0
                    val paidAt = child.child("paidAt").getValue(Long::class.java) ?: 0L
                    val id = child.child("paymentId").getValue(String::class.java) ?: child.key.orEmpty()
                    HistoryItem(id, "Membership renewal", amount, paidAt)
                }

                val merged = (unified + legacy)
                    .distinctBy { it.id }
                    .sortedByDescending { it.paidAt }

                entries = merged.map { it.toEntry() }
            } catch (_: Exception) {
                entries = emptyList()
            } finally { isLoading = false }
        }
    }

    private data class HistoryItem(
        val id: String,
        val title: String,
        val amount: Int,
        val paidAt: Long
    ) {
        fun toEntry(): PaymentHistoryEntry {
            val date = if (paidAt > 0) {
                SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date(paidAt))
            } else {
                "Date unavailable"
            }
            return PaymentHistoryEntry(title, amount, date, id)
        }
    }
}
