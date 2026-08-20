package com.qwerty.morningstarfitness.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.qwerty.morningstarfitness.models.AttendanceEntry
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AttendanceViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")
    val attendanceHistory = mutableStateListOf<AttendanceEntry>()
    private var lastCheckInDay: String? = null
    var lastCheckInError by mutableStateOf<String?>(null); private set

    init { fetchAttendanceHistory() }

    suspend fun recordCheckIn(): Boolean {
        lastCheckInError = null
        val uid = auth.currentUser?.uid ?: run { lastCheckInError = "You must be logged in to record a visit."; return false }
        val member = try { database.reference.child("members").child(uid).get().await() } catch (_: Exception) { lastCheckInError = "Could not verify your membership. Please try again."; return false }
        val expiry = member.child("membershipExpiry").getValue(String::class.java)
        if (expiry.isNullOrBlank() || isExpired(expiry)) { lastCheckInError = "Your membership has expired. Please renew before checking in."; return false }
        val now = Date(); val dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now)
        if (dayKey == lastCheckInDay) return false
        val dateLabel = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(now)
        val timeLabel = SimpleDateFormat("h:mm a", Locale.getDefault()).format(now)
        val entry = AttendanceEntry(dateLabel, timeLabel, timestamp = now.time)
        return try {
            val ref = database.reference.child("members").child(uid).child("attendance").child(dayKey)
            if (ref.get().await().exists()) { lastCheckInDay = dayKey; return false }
            ref.setValue(mapOf("date" to entry.date, "checkIn" to entry.checkIn, "checkOut" to entry.checkOut, "timestamp" to entry.timestamp)).await()
            attendanceHistory.add(0, entry); lastCheckInDay = dayKey; true
        } catch (e: Exception) { lastCheckInError = e.message ?: "Could not save the visit. Please try again."; false }
    }

    fun fetchAttendanceHistory() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = database.reference.child("members").child(uid).child("attendance").get().await()
                val entries = snapshot.children.mapNotNull { child ->
                    val date = child.child("date").getValue(String::class.java) ?: return@mapNotNull null
                    AttendanceEntry(date, child.child("checkIn").getValue(String::class.java).orEmpty(), child.child("checkOut").getValue(String::class.java), child.child("timestamp").getValue(Long::class.java) ?: 0L)
                }.sortedByDescending { it.timestamp }
                attendanceHistory.clear(); attendanceHistory.addAll(entries)
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (snapshot.child(today).exists()) lastCheckInDay = today
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun clearHistory() { attendanceHistory.clear(); lastCheckInDay = null; lastCheckInError = null }

    private fun isExpired(value: String): Boolean = try {
        val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) ?: return true
        val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY,0); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0) }.time
        expiry.before(today)
    } catch (_: Exception) { true }
}
