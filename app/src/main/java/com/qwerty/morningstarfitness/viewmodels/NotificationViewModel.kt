package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.qwerty.morningstarfitness.ui.screens.notifications.NotificationItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")

    var notifications by mutableStateOf<List<NotificationItem>>(emptyList()); private set
    var unreadCount by mutableIntStateOf(0); private set
    var isLoading by mutableStateOf(false); private set
    var loadError by mutableStateOf<String?>(null); private set

    fun refresh() {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true; loadError = null
        viewModelScope.launch {
            try {
                val member = database.reference.child("members").child(uid).get().await()
                val memberId = member.child("memberId").getValue(String::class.java).orEmpty()
                if (memberId.isBlank()) throw Exception("Member ID is missing.")
                val snapshot = database.reference.child("notifications").child(memberId).get().await()
                val records = snapshot.children.mapNotNull { child ->
                    val title = child.child("title").getValue(String::class.java) ?: return@mapNotNull null
                    val message = child.child("message").getValue(String::class.java).orEmpty()
                    val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L
                    val read = child.child("read").getValue(Boolean::class.java) ?: false
                    val time = if (createdAt > 0L) SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()).format(Date(createdAt)) else ""
                    NotificationRecord(child.key.orEmpty(), title, message, time, createdAt, read)
                }.sortedByDescending { it.createdAt }
                unreadCount = records.count { !it.read }
                notifications = records.map { NotificationItem(it.title, it.message, it.time) }
                createExpiryReminderIfNeeded(memberId, member)
            } catch (e: Exception) {
                loadError = e.message ?: "Could not load notifications."
            } finally { isLoading = false }
        }
    }

    private suspend fun createExpiryReminderIfNeeded(memberId: String, member: com.google.firebase.database.DataSnapshot) {
        val expiry = member.child("membershipExpiry").getValue(String::class.java) ?: return
        val expiryDate = runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expiry) }.getOrNull() ?: return
        val daysLeft = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(expiryDate.time - System.currentTimeMillis()).toInt()
        if (!(daysLeft in 0..5)) return
        val key = "expiry_$expiry"
        val ref = database.reference.child("notifications").child(memberId).child(key)
        if (!ref.get().await().exists()) {
            val message = if (daysLeft == 0) "Your membership expires today. Renew to keep gym access." else "Your membership expires in $daysLeft day${if (daysLeft == 1) "" else "s"}. Renew to keep gym access."
            ref.setValue(mapOf("title" to "Membership reminder", "message" to message, "type" to "membership_expiry", "read" to false, "createdAt" to System.currentTimeMillis())).await()
        }
    }

    fun markAllRead() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val member = database.reference.child("members").child(uid).get().await()
            val memberId = member.child("memberId").getValue(String::class.java).orEmpty()
            if (memberId.isBlank()) return@launch
            val snapshot = database.reference.child("notifications").child(memberId).get().await()
            val updates = buildMap<String, Any> {
                snapshot.children.forEach { child -> put("${child.key}/read", true) }
            }
            if (updates.isNotEmpty()) database.reference.child("notifications").child(memberId).updateChildren(updates).await()
            refresh()
        }
    }

    private data class NotificationRecord(val id: String, val title: String, val message: String, val time: String, val createdAt: Long, val read: Boolean)
}
