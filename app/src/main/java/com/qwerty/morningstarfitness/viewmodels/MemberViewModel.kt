package com.qwerty.morningstarfitness.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.qwerty.morningstarfitness.data.MemberDataStore
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class MemberRefreshStatus { SUCCESS, NOT_FOUND, FAILURE }

class MemberViewModel(application: Application) : AndroidViewModel(application) {
    private val store = MemberDataStore(application)
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")

    var memberForm by mutableStateOf<MemberFormState?>(null); private set
    var selectedPlan by mutableStateOf<MembershipPlanModel?>(null); private set
    var paymentMethod by mutableStateOf("mpesa"); private set
    var paymentStatus by mutableStateOf("pending"); private set
    var qrCodeValue by mutableStateOf<String?>(null); private set
    var isLoaded by mutableStateOf(false); private set
    var memberId by mutableStateOf<String?>(null); private set
    var membershipStart by mutableStateOf<String?>(null); private set
    var membershipExpiry by mutableStateOf<String?>(null); private set
    var renewalError by mutableStateOf<String?>(null); private set
    var isRenewing by mutableStateOf(false); private set
    var profileSaveError by mutableStateOf<String?>(null); private set

    init { restoreSavedMember() }

    private fun stableMemberId(uid: String): String = "MSFC-" + uid.take(8).uppercase()

    private suspend fun migrateExistingMember(snapshot: DataSnapshot): String {
        val uid = auth.currentUser?.uid ?: return snapshot.child("memberId").getValue(String::class.java).orEmpty()
        val existing = snapshot.child("memberId").getValue(String::class.java).orEmpty()
        val migratedId = when {
            existing.startsWith("MSFC-", ignoreCase = true) -> existing.uppercase()
            existing.isNotBlank() -> "MSFC-" + existing.removePrefix("MSF-").take(8).uppercase().ifBlank { uid.take(8).uppercase() }
            else -> stableMemberId(uid)
        }
        snapshot.ref.updateChildren(mapOf("memberId" to migratedId, "memberRecordVersion" to 3)).await()
        return migratedId
    }

    private fun applySnapshot(s: DataSnapshot, normalizedMemberId: String? = null) {
        memberForm = MemberFormState(
            fullName = s.child("fullName").getValue(String::class.java).orEmpty(),
            phone = s.child("phone").getValue(String::class.java).orEmpty(),
            email = s.child("email").getValue(String::class.java).orEmpty(),
            dob = s.child("dob").getValue(String::class.java).orEmpty(),
            gender = s.child("gender").getValue(String::class.java).orEmpty(),
            emergencyContact = s.child("emergencyContact").getValue(String::class.java).orEmpty(),
            securityQuestion = s.child("securityQuestion").getValue(String::class.java).orEmpty(),
            securityAnswer = s.child("securityAnswer").getValue(String::class.java).orEmpty(),
            profilePictureUrl = s.child("profilePictureUrl").getValue(String::class.java)
        )
        qrCodeValue = s.child("qrCode").getValue(String::class.java)
        memberId = normalizedMemberId ?: s.child("memberId").getValue(String::class.java)
        membershipStart = s.child("membershipStart").getValue(String::class.java)
        membershipExpiry = s.child("membershipExpiry").getValue(String::class.java)
        paymentStatus = s.child("paymentStatus").getValue(String::class.java) ?: "pending"
        paymentMethod = s.child("paymentMethod").getValue(String::class.java) ?: "mpesa"
        val planId = s.child("planId").getValue(String::class.java)
        selectedPlan = if (planId.isNullOrBlank()) null else MembershipPlanModel(planId, s.child("planLabel").getValue(String::class.java).orEmpty(), s.child("planPrice").getValue(Long::class.java)?.toInt() ?: 0, s.child("planDuration").getValue(Long::class.java)?.toInt() ?: 0)
    }

    private suspend fun migrateLegacyRecords(uid: String, memberId: String, memberSnapshot: DataSnapshot) {
        val root = database.reference
        val memberName = memberSnapshot.child("fullName").getValue(String::class.java).orEmpty()
        val payments = memberSnapshot.child("payments")
        for (child in payments.children) {
            val paymentId = child.child("paymentId").getValue(String::class.java) ?: child.key ?: continue
            val existing = root.child("payments").child(paymentId).get().await()
            if (!existing.exists()) {
                val data = child.value as? Map<*, *> ?: emptyMap<String, Any?>()
                val normalized = mutableMapOf<String, Any?>()
                data.forEach { (k, v) -> if (k is String) normalized[k] = v }
                normalized["paymentId"] = paymentId
                normalized["memberId"] = memberId
                normalized["memberUid"] = uid
                normalized["memberName"] = memberName
                root.child("payments").child(paymentId).setValue(normalized).await()
            }
        }

        val legacyHistory = memberSnapshot.child("paymentHistory")
        for (child in legacyHistory.children) {
            val paymentId = child.child("paymentId").getValue(String::class.java) ?: child.key ?: continue
            val existing = root.child("payments").child(paymentId).get().await()
            if (!existing.exists()) {
                val normalized = mutableMapOf<String, Any?>()
                normalized["paymentId"] = paymentId
                normalized["memberId"] = memberId
                normalized["memberUid"] = uid
                normalized["memberName"] = memberName
                normalized["paymentReference"] = child.child("referenceId").getValue(String::class.java)
                normalized["mpesaReceipt"] = child.child("receipt").getValue(String::class.java)
                normalized["type"] = "membership_renewal"
                normalized["amountKsh"] = child.child("amountKsh").getValue(Long::class.java)?.toInt() ?: 0
                normalized["status"] = child.child("status").getValue(String::class.java) ?: "paid"
                normalized["method"] = child.child("method").getValue(String::class.java) ?: "sandbox_demo"
                normalized["paidAt"] = child.child("paidAt").getValue(Long::class.java) ?: 0L
                normalized["environment"] = child.child("environment").getValue(String::class.java) ?: "presentation"
                root.child("payments").child(paymentId).setValue(normalized).await()
            }
        }

        val legacyAttendance = memberSnapshot.child("attendance")
        for (child in legacyAttendance.children) {
            val dayKey = child.key ?: continue
            val attendanceRef = root.child("attendance").child(memberId).child(dayKey)
            if (!attendanceRef.get().await().exists()) {
                val normalized = mutableMapOf<String, Any?>()
                normalized["memberId"] = memberId
                normalized["memberUid"] = uid
                normalized["date"] = child.child("date").getValue(String::class.java).orEmpty()
                normalized["checkIn"] = child.child("checkIn").getValue(String::class.java).orEmpty()
                normalized["checkOut"] = child.child("checkOut").getValue(String::class.java)
                normalized["status"] = "present"
                normalized["timestamp"] = child.child("timestamp").getValue(Long::class.java) ?: 0L
                attendanceRef.setValue(normalized).await()
            }
        }
    }

    suspend fun refreshFromFirebase(): MemberRefreshStatus {
        val uid = auth.currentUser?.uid ?: return MemberRefreshStatus.FAILURE
        return try {
            var snapshot = database.reference.child("members").child(uid).get().await()
            if (!snapshot.exists()) { 
                memberForm = null; memberId = null; qrCodeValue = null; selectedPlan = null; isLoaded = true
                return MemberRefreshStatus.NOT_FOUND 
            }
            val normalizedId = migrateExistingMember(snapshot)
            snapshot = database.reference.child("members").child(uid).get().await()
            applySnapshot(snapshot, normalizedId); persist(); migrateLegacyRecords(uid, normalizedId, snapshot); isLoaded = true
            MemberRefreshStatus.SUCCESS
        } catch (e: Exception) { 
            profileSaveError = e.message ?: "Could not refresh member details."; isLoaded = true
            MemberRefreshStatus.FAILURE 
        }
    }

    suspend fun loadLocalMember(): Boolean {
        return try {
            val preferences = store.read()
            fun get(key: String): String = preferences.entries.firstOrNull { it.key.name == key }?.value.orEmpty()
            val savedUid = get("auth_uid")
            val currentUid = auth.currentUser?.uid
            
            // If not logged in, or if logged in as a different user, clear local cache.
            if (currentUid == null || (savedUid.isNotBlank() && savedUid != currentUid)) {
                store.clear()
                memberForm = null
                selectedPlan = null
                memberId = null
                qrCodeValue = null
                membershipStart = null
                membershipExpiry = null
                isLoaded = true
                return false
            }

            val name = get("full_name")
            if (name.isBlank()) { isLoaded = true; return false }
            memberForm = MemberFormState(
                fullName = name,
                phone = get("phone"),
                email = get("email"),
                dob = get("dob"),
                gender = get("gender"),
                emergencyContact = get("emergency_contact"),
                securityQuestion = get("security_question"),
                securityAnswer = get("security_answer"),
                profilePictureUrl = get("profile_picture_url").ifBlank { null }
            )
            val label = get("selected_plan_label")
            selectedPlan = if (label.isBlank()) null else MembershipPlanModel(get("selected_plan_id"), label, get("selected_plan_price").toIntOrNull() ?: 0, get("selected_plan_duration").toIntOrNull() ?: 0)
            paymentMethod = get("payment_method").ifBlank { "mpesa" }; paymentStatus = get("payment_status").ifBlank { "pending" }; qrCodeValue = get("qr_code").ifBlank { null }; memberId = get("member_id").ifBlank { null }; membershipStart = get("membership_start").ifBlank { null }; membershipExpiry = get("membership_expiry").ifBlank { null }; isLoaded = true; true
        } catch (e: Exception) { profileSaveError = e.message ?: "Could not load saved member details."; isLoaded = true; false }
    }

    fun syncWithFirebase() { viewModelScope.launch { refreshFromFirebase() } }
    private fun restoreSavedMember() { viewModelScope.launch { loadLocalMember(); if (auth.currentUser != null) refreshFromFirebase() } }
    fun updateMemberForm(form: MemberFormState) { memberForm = form; persist() }
    fun updateSelectedPlan(plan: MembershipPlanModel) { selectedPlan = plan; persist() }
    fun updatePaymentMethod(method: String) { paymentMethod = method; persist() }
    fun updateProfile(form: MemberFormState, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        profileSaveError = null
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(false, "You are not signed in.")
            return
        }
        viewModelScope.launch {
            try {
                database.reference.child("members").child(uid).updateChildren(
                    mapOf(
                        "fullName" to form.fullName.trim(),
                        "phone" to form.phone.trim(),
                        "email" to form.email.trim(),
                        "dob" to form.dob,
                        "gender" to form.gender,
                        "emergencyContact" to form.emergencyContact.trim(),
                        "profilePictureUrl" to form.profilePictureUrl
                    )
                ).await()
                refreshFromFirebase()
                onResult(true, "Profile updated successfully.")
            } catch (e: Exception) {
                profileSaveError = e.message ?: "Could not save profile."
                onResult(false, profileSaveError ?: "Could not save profile.")
            }
        }
    }
    fun prepareMembership(plan: MembershipPlanModel) { val now = System.currentTimeMillis(); val calendar = Calendar.getInstance(); membershipStart = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now)); calendar.timeInMillis = now; calendar.add(Calendar.MONTH, plan.durationMonths); membershipExpiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time); memberId = "MSFC-" + UUID.randomUUID().toString().take(6).uppercase(); persist() }
    fun generateQrCode(forceNew: Boolean = false): String { val existing = qrCodeValue?.takeIf { it.isNotBlank() }; if (!forceNew && existing != null) return existing; val value = "GYM-" + UUID.randomUUID().toString(); qrCodeValue = value; persist(); return value }
    fun completePaymentLocally() { paymentStatus = "paid"; persist() }
    fun ensureMembershipQr(): String? = qrCodeValue
    fun getMembershipStatus(): String { if (selectedPlan == null) return "No Plan"; val expiry = membershipExpiry ?: return "Pending"; return if (isExpired(expiry)) "Expired" else "Active" }
    private fun isExpired(value: String): Boolean = try { val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) ?: return true; val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time; expiry.before(today) } catch (_: Exception) { true }
    fun renewMembership(plan: MembershipPlanModel, onComplete: (Boolean) -> Unit) { renewalError = "Use the M-Pesa payment screen to renew membership."; onComplete(false) }
    private fun persist() {
        val member = memberForm ?: return
        viewModelScope.launch {
            store.saveMember(
                authUid = auth.currentUser?.uid,
                fullName = member.fullName,
                phone = member.phone,
                email = member.email,
                dob = member.dob,
                gender = member.gender,
                emergencyContact = member.emergencyContact,
                securityQuestion = member.securityQuestion,
                securityAnswer = member.securityAnswer,
                planId = selectedPlan?.id,
                planLabel = selectedPlan?.label,
                planPrice = selectedPlan?.priceKsh,
                planDuration = selectedPlan?.durationMonths,
                paymentMethod = paymentMethod,
                paymentStatus = paymentStatus,
                qrCode = qrCodeValue,
                memberId = memberId,
                membershipStart = membershipStart,
                membershipExpiry = membershipExpiry,
                profilePictureUrl = member.profilePictureUrl
            )
        }
    }
    fun clearLocalData() { viewModelScope.launch { store.clear() }; memberForm = null; selectedPlan = null; memberId = null; qrCodeValue = null; membershipStart = null; membershipExpiry = null; paymentMethod = "mpesa"; paymentStatus = "pending"; renewalError = null; profileSaveError = null; isLoaded = true }
    fun verifySecurityAnswer(answer: String): Boolean { val form = memberForm ?: return false; return com.qwerty.morningstarfitness.security.hashSecurityAnswer(answer) == form.securityAnswer }
}
