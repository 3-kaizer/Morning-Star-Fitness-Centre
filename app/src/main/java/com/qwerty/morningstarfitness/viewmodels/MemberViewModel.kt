package com.qwerty.morningstarfitness.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    init { restoreSavedMember(); syncWithFirebase() }

    fun syncWithFirebase() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val s = database.reference.child("members").child(uid).get().await()
                if (s.exists()) {
                    memberForm = MemberFormState(
                        fullName = s.child("fullName").getValue(String::class.java).orEmpty(),
                        phone = s.child("phone").getValue(String::class.java).orEmpty(),
                        email = s.child("email").getValue(String::class.java).orEmpty(),
                        dob = s.child("dob").getValue(String::class.java).orEmpty(),
                        gender = s.child("gender").getValue(String::class.java).orEmpty(),
                        emergencyContact = s.child("emergencyContact").getValue(String::class.java).orEmpty(),
                        securityQuestion = s.child("securityQuestion").getValue(String::class.java).orEmpty(),
                        securityAnswer = s.child("securityAnswer").getValue(String::class.java).orEmpty()
                    )
                    qrCodeValue = s.child("qrCode").getValue(String::class.java)
                    memberId = s.child("memberId").getValue(String::class.java)
                    membershipStart = s.child("membershipStart").getValue(String::class.java)
                    membershipExpiry = s.child("membershipExpiry").getValue(String::class.java)
                    paymentStatus = s.child("paymentStatus").getValue(String::class.java) ?: "pending"
                    paymentMethod = s.child("paymentMethod").getValue(String::class.java) ?: "mpesa"
                    val planId = s.child("planId").getValue(String::class.java)
                    if (!planId.isNullOrBlank()) selectedPlan = MembershipPlanModel(
                        id = planId,
                        label = s.child("planLabel").getValue(String::class.java).orEmpty(),
                        priceKsh = s.child("planPrice").getValue(Long::class.java)?.toInt() ?: 0,
                        durationMonths = s.child("planDuration").getValue(Long::class.java)?.toInt() ?: 0
                    )
                    persist()
                }
            } catch (e: Exception) { e.printStackTrace() }
            isLoaded = true
        }
    }

    private fun restoreSavedMember() {
        viewModelScope.launch {
            val p = store.read()
            val name = p.entries.firstOrNull { it.key.name == "full_name" }?.value
            if (name != null) {
                fun get(k: String) = p.entries.firstOrNull { it.key.name == k }?.value.orEmpty()
                memberForm = MemberFormState(
                    fullName = get("full_name"),
                    phone = get("phone"),
                    email = get("email"),
                    dob = get("dob"),
                    gender = get("gender"),
                    emergencyContact = get("emergency_contact"),
                    securityQuestion = get("security_question"),
                    securityAnswer = get("security_answer")
                )
                val label = get("selected_plan_label")
                if (label.isNotBlank()) selectedPlan = MembershipPlanModel(get("selected_plan_id"), label, get("selected_plan_price").toIntOrNull() ?: 0, get("selected_plan_duration").toIntOrNull() ?: 0)
                paymentMethod = get("payment_method").ifBlank { "mpesa" }
                paymentStatus = get("payment_status").ifBlank { "pending" }
                qrCodeValue = get("qr_code").ifBlank { null }; memberId = get("member_id").ifBlank { null }
                membershipStart = get("membership_start").ifBlank { null }; membershipExpiry = get("membership_expiry").ifBlank { null }
            }
            isLoaded = true
        }
    }

    fun updateMemberForm(form: MemberFormState) { memberForm = form; persist() }
    fun updateSelectedPlan(plan: MembershipPlanModel) { selectedPlan = plan; persist() }
    fun updatePaymentMethod(method: String) { paymentMethod = method; persist() }

    fun updateProfile(form: MemberFormState) {
        memberForm = form; persist(); val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch { try { database.reference.child("members").child(uid).updateChildren(mapOf("fullName" to form.fullName.trim(), "phone" to form.phone.trim(), "email" to form.email.trim(), "dob" to form.dob, "gender" to form.gender, "emergencyContact" to form.emergencyContact.trim())).await() } catch (e: Exception) { e.printStackTrace() } }
    }

    fun prepareMembership(plan: MembershipPlanModel) {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        membershipStart = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now))
        calendar.timeInMillis = now
        calendar.add(Calendar.MONTH, plan.durationMonths)
        membershipExpiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
        memberId = "MSF-" + UUID.randomUUID().toString().take(6).uppercase()
        persist()
    }

    fun generateQrCode(): String {
        val value = "GYM-" + UUID.randomUUID().toString()
        qrCodeValue = value
        persist()
        return value
    }

    fun completePaymentLocally(plan: MembershipPlanModel) {
        paymentStatus = "paid"
        persist()
    }

    fun ensureMembershipQr(): String? {
        return qrCodeValue
    }

    fun getMembershipStatus(): String {
        if (selectedPlan == null) return "No Plan"
        val expiry = membershipExpiry ?: return "Pending"
        return if (isExpired(expiry)) "Expired" else "Active"
    }

    private fun isExpired(value: String): Boolean = try {
        val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) ?: return true
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        !expiry.after(today) && !SimpleDateFormat("yyyy-MM-dd", Locale.US).format(expiry).equals(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(today))
        // Wait, the logic should be: if today is after expiry date.
        // If expiry is 2026-08-20 and today is 2026-08-20, it's NOT expired yet (it expires at end of day usually, or we treat it as active on the expiry day).
        expiry.before(today)
    } catch (_: Exception) { true }

    fun renewMembership(plan: MembershipPlanModel, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { renewalError = "Not logged in"; onComplete(false); return }
        renewalError = null
        isRenewing = true
        
        viewModelScope.launch {
            try {
                // In this demo, we assume the payment was already simulated successfully 
                // and we are just updating the record.
                val memberRef = database.reference.child("members").child(uid)
                val snapshot = memberRef.get().await()
                val existingExpiry = snapshot.child("membershipExpiry").getValue(String::class.java)
                
                val calendar = Calendar.getInstance()
                val today = Date()
                val existing = existingExpiry?.let {
                    runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it) }.getOrNull()
                }
                
                val renewalBase = if (existing != null && existing.after(today)) existing else today
                calendar.time = renewalBase
                calendar.add(Calendar.MONTH, plan.durationMonths)
                
                val newStart = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(renewalBase)
                val newExpiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                
                val paymentId = "RENEW-" + UUID.randomUUID().toString().take(8).uppercase()
                val now = System.currentTimeMillis()
                
                val updates = mapOf(
                    "planId" to plan.id,
                    "planLabel" to plan.label,
                    "planPrice" to plan.priceKsh,
                    "planDuration" to plan.durationMonths,
                    "membershipStart" to newStart,
                    "membershipExpiry" to newExpiry,
                    "paymentStatus" to "paid",
                    "lastPaymentId" to paymentId,
                    "lastPaymentAt" to now
                )
                
                memberRef.updateChildren(updates).await()
                
                // Add to payment history
                memberRef.child("payments").child(paymentId).setValue(
                    mapOf(
                        "paymentId" to paymentId,
                        "type" to "membership_renewal",
                        "planId" to plan.id,
                        "planLabel" to plan.label,
                        "amountKsh" to plan.priceKsh,
                        "status" to "paid",
                        "method" to "demo_mpesa",
                        "paidAt" to now,
                        "membershipStart" to newStart,
                        "membershipExpiry" to newExpiry
                    )
                ).await()
                
                syncWithFirebase()
                onComplete(true)
            } catch (e: Exception) {
                renewalError = e.message ?: "Renewal failed"
                onComplete(false)
            } finally {
                isRenewing = false
            }
        }
    }

    private fun persist() { val member = memberForm ?: return; viewModelScope.launch { store.saveMember(member.fullName, member.phone, member.email, member.dob, member.gender, member.emergencyContact, member.securityQuestion, member.securityAnswer, selectedPlan?.id, selectedPlan?.label, selectedPlan?.priceKsh, selectedPlan?.durationMonths, paymentMethod, paymentStatus, qrCodeValue, memberId, membershipStart, membershipExpiry) } }

    fun clearLocalData() { memberForm = null; selectedPlan = null; paymentMethod = "mpesa"; paymentStatus = "pending"; qrCodeValue = null; memberId = null; membershipStart = null; membershipExpiry = null; renewalError = null; viewModelScope.launch { store.clear() } }

    fun verifySecurityAnswer(answer: String): Boolean {
        val form = memberForm ?: return false
        val hashedInput = com.qwerty.morningstarfitness.security.hashSecurityAnswer(answer)
        return hashedInput == form.securityAnswer
    }
}
