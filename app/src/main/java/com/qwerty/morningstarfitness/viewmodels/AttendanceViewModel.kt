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
    var isLoading by mutableStateOf(false); private set
    var loadError by mutableStateOf<String?>(null); private set

    private suspend fun memberIdentity(): Pair<String, String>? {
        val uid = auth.currentUser?.uid ?: return null
        val member = database.reference.child("members").child(uid).get().await()
        val memberId = member.child("memberId").getValue(String::class.java).orEmpty()
        if (!member.exists() || memberId.isBlank()) return null
        return uid to memberId
    }

    suspend fun recordCheckIn(): Boolean {
        lastCheckInError = null
        val identity = try { memberIdentity() } catch (e: Exception) {
            lastCheckInError = e.message ?: "Could not verify your membership."; return false
        } ?: run { lastCheckInError = "Your member profile could not be verified."; return false }
        val (uid, memberId) = identity
        return try {
            val member = database.reference.child("members").child(uid).get().await()
            val expiry = member.child("membershipExpiry").getValue(String::class.java)
            if (expiry.isNullOrBlank() || isExpired(expiry)) {
                lastCheckInError = "Your membership has expired. Please renew before entering the gym."
                return false
            }

            val now = Date()
            val dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now)
            val attendanceRef = database.reference.child("attendance").child(memberId).child(dayKey)
            if (dayKey == lastCheckInDay || attendanceRef.get().await().exists()) {
                lastCheckInDay = dayKey
                lastCheckInError = "Today's gym entry has already been recorded."
                return false
            }

            val entry = AttendanceEntry(
                date = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(now),
                checkIn = SimpleDateFormat("h:mm a", Locale.getDefault()).format(now),
                timestamp = now.time
            )
            attendanceRef.setValue(
                mapOf(
                    "memberId" to memberId,
                    "memberUid" to uid,
                    "date" to entry.date,
                    "checkIn" to entry.checkIn,
                    "checkOut" to entry.checkOut,
                    "status" to "present",
                    "timestamp" to entry.timestamp
                )
            ).await()

            attendanceHistory.removeAll { it.timestamp == entry.timestamp }
            attendanceHistory.add(0, entry)
            lastCheckInDay = dayKey
            true
        } catch (e: Exception) {
            lastCheckInError = e.message ?: "Could not save the gym entry. Please try again."
            false
        }
    }

    fun fetchAttendanceHistory() {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true
        loadError = null
        viewModelScope.launch {
            try {
                val member = database.reference.child("members").child(uid).get().await()
                val memberId = member.child("memberId").getValue(String::class.java).orEmpty()
                if (memberId.isBlank()) throw Exception("Member ID is missing.")

                val newSnapshot = database.reference.child("attendance").child(memberId).get().await()
                val entries = newSnapshot.children.mapNotNull { child ->
                    val date = child.child("date").getValue(String::class.java) ?: return@mapNotNull null
                    AttendanceEntry(
                        date = date,
                        checkIn = child.child("checkIn").getValue(String::class.java).orEmpty(),
                        checkOut = child.child("checkOut").getValue(String::class.java),
                        timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    )
                }.sortedByDescending { it.timestamp }

                // Backward compatibility: show old member-scoped attendance until migrated data is gone.
                val legacySnapshot = database.reference.child("members").child(uid).child("attendance").get().await()
                val legacyEntries = legacySnapshot.children.mapNotNull { child ->
                    val date = child.child("date").getValue(String::class.java) ?: return@mapNotNull null
                    AttendanceEntry(
                        date = date,
                        checkIn = child.child("checkIn").getValue(String::class.java).orEmpty(),
                        checkOut = child.child("checkOut").getValue(String::class.java),
                        timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    )
                }

                attendanceHistory.clear()
                attendanceHistory.addAll((entries + legacyEntries).distinctBy { it.timestamp }.sortedByDescending { it.timestamp })
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (newSnapshot.child(today).exists() || legacySnapshot.child(today).exists()) lastCheckInDay = today
            } catch (e: Exception) {
                loadError = e.message ?: "Could not load attendance."
            } finally { isLoading = false }
        }
    }

    fun clearHistory() {
        attendanceHistory.clear()
        lastCheckInDay = null
        lastCheckInError = null
        loadError = null
    }

    private fun isExpired(value: String): Boolean = try {
        val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) ?: return true
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.time
        expiry.before(today)
    } catch (_: Exception) { true }
}
