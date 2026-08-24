package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    fun refresh() {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true
        viewModelScope.launch {
            try {
                val snapshot = database.reference.child("members").child(uid).child("payments").get().await()
                entries = snapshot.children.map { child ->
                    val type = child.child("type").getValue(String::class.java).orEmpty()
                    val planLabel = child.child("planLabel").getValue(String::class.java).orEmpty()
                    val title = if (type == "membership_renewal") "Membership renewal — $planLabel" else "Membership registration — $planLabel"
                    val amount = child.child("amountKsh").getValue(Long::class.java)?.toInt() ?: 0
                    val paidAt = child.child("paidAt").getValue(Long::class.java) ?: 0L
                    val date = if (paidAt > 0) SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date(paidAt)) else "Date unavailable"
                    PaymentHistoryEntry(title, amount, date, child.child("paymentId").getValue(String::class.java) ?: child.key.orEmpty())
                }.reversed()
            } catch (_: Exception) {
                entries = emptyList()
            } finally { isLoading = false }
        }
    }
}
