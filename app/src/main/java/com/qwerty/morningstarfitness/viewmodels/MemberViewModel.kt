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
    var profileSaveError by mutableStateOf<String?>(null); private set

    init { restoreSavedMember() }

    private fun applySnapshot(s: com.google.firebase.database.DataSnapshot) {
        memberForm = MemberFormState(
            fullName = s.child("fullName").getValue(String::class.java).orEmpty(), phone = s.child("phone").getValue(String::class.java).orEmpty(), email = s.child("email").getValue(String::class.java).orEmpty(), dob = s.child("dob").getValue(String::class.java).orEmpty(), gender = s.child("gender").getValue(String::class.java).orEmpty(), emergencyContact = s.child("emergencyContact").getValue(String::class.java).orEmpty(), securityQuestion = s.child("securityQuestion").getValue(String::class.java).orEmpty(), securityAnswer = s.child("securityAnswer").getValue(String::class.java).orEmpty())
        qrCodeValue = s.child("qrCode").getValue(String::class.java); memberId = s.child("memberId").getValue(String::class.java); membershipStart = s.child("membershipStart").getValue(String::class.java); membershipExpiry = s.child("membershipExpiry").getValue(String::class.java); paymentStatus = s.child("paymentStatus").getValue(String::class.java) ?: "pending"; paymentMethod = s.child("paymentMethod").getValue(String::class.java) ?: "mpesa"
        val planId = s.child("planId").getValue(String::class.java)
        selectedPlan = if (planId.isNullOrBlank()) null else MembershipPlanModel(planId, s.child("planLabel").getValue(String::class.java).orEmpty(), s.child("planPrice").getValue(Long::class.java)?.toInt() ?: 0, s.child("planDuration").getValue(Long::class.java)?.toInt() ?: 0)
    }

    suspend fun refreshFromFirebase(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val snapshot = database.reference.child("members").child(uid).get().await()
            if (!snapshot.exists()) { isLoaded = true; return false }
            applySnapshot(snapshot); persist(); isLoaded = true; true
        } catch (e: Exception) { profileSaveError = e.message ?: "Could not refresh member details."; isLoaded = true; false }
    }

    suspend fun loadLocalMember(): Boolean {
        return try {
            val p = store.read(); fun get(k: String) = p.entries.firstOrNull { it.key.name == k }?.value.orEmpty(); val name = get("full_name")
            if (name.isBlank()) { isLoaded = true; return false }
            memberForm = MemberFormState(name, get("phone"), get("email"), get("dob"), get("gender"), get("emergency_contact"), get("security_question"), get("security_answer"))
            val label = get("selected_plan_label")
            selectedPlan = if (label.isBlank()) null else MembershipPlanModel(get("selected_plan_id"), label, get("selected_plan_price").toIntOrNull() ?: 0, get("selected_plan_duration").toIntOrNull() ?: 0)
            paymentMethod = get("payment_method").ifBlank { "mpesa" }; paymentStatus = get("payment_status").ifBlank { "pending" }; qrCodeValue = get("qr_code").ifBlank { null }; memberId = get("member_id").ifBlank { null }; membershipStart = get("membership_start").ifBlank { null }; membershipExpiry = get("membership_expiry").ifBlank { null }; isLoaded = true; true
        } catch (e: Exception) { profileSaveError = e.message ?: "Could not load saved member details."; isLoaded = true; false }
    }

    fun syncWithFirebase() { viewModelScope.launch { refreshFromFirebase() } }
    private fun restoreSavedMember() { viewModelScope.launch { loadLocalMember() } }
    fun updateMemberForm(form: MemberFormState) { memberForm = form; persist() }
    fun updateSelectedPlan(plan: MembershipPlanModel) { selectedPlan = plan; persist() }
    fun updatePaymentMethod(method: String) { paymentMethod = method; persist() }

    fun updateProfile(form: MemberFormState, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        profileSaveError = null; val uid = auth.currentUser?.uid
        if (uid == null) { onResult(false, "You are not signed in."); return }
        viewModelScope.launch { try { database.reference.child("members").child(uid).updateChildren(mapOf("fullName" to form.fullName.trim(), "phone" to form.phone.trim(), "email" to form.email.trim(), "dob" to form.dob, "gender" to form.gender, "emergencyContact" to form.emergencyContact.trim())).await(); refreshFromFirebase(); onResult(true, "Profile updated successfully.") } catch (e: Exception) { profileSaveError = e.message ?: "Could not save profile."; onResult(false, profileSaveError ?: "Could not save profile.") } }
    }

    fun prepareMembership(plan: MembershipPlanModel) { val now = System.currentTimeMillis(); val calendar = Calendar.getInstance(); membershipStart = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now)); calendar.timeInMillis = now; calendar.add(Calendar.MONTH, plan.durationMonths); membershipExpiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time); memberId = "MSF-" + UUID.randomUUID().toString().take(6).uppercase(); persist() }
    fun generateQrCode(forceNew: Boolean = false): String { val existing = qrCodeValue?.takeIf { it.isNotBlank() }; if (!forceNew && existing != null) return existing; val value = "GYM-" + UUID.randomUUID().toString(); qrCodeValue = value; persist(); return value }
    fun completePaymentLocally(plan: MembershipPlanModel) { paymentStatus = "paid"; persist() }
    fun ensureMembershipQr(): String? = qrCodeValue
    fun getMembershipStatus(): String { if (selectedPlan == null) return "No Plan"; val expiry = membershipExpiry ?: return "Pending"; return if (isExpired(expiry)) "Expired" else "Active" }
    private fun isExpired(value: String): Boolean = try { val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) ?: return true; val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time; expiry.before(today) } catch (_: Exception) { true }

    fun renewMembership(plan: MembershipPlanModel, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { renewalError = "Not logged in"; onComplete(false); return }; renewalError = null; isRenewing = true
        viewModelScope.launch { try { val memberRef = database.reference.child("members").child(uid); val snapshot = memberRef.get().await(); val existingExpiry = snapshot.child("membershipExpiry").getValue(String::class.java); val calendar = Calendar.getInstance(); val today = Date(); val existing = existingExpiry?.let { runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it) }.getOrNull() }; val renewalBase = if (existing != null && existing.after(today)) existing else today; calendar.time = renewalBase; calendar.add(Calendar.MONTH, plan.durationMonths); val newStart = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(renewalBase); val newExpiry = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time); val paymentId = "RENEW-" + UUID.randomUUID().toString().take(8).uppercase(); val now = System.currentTimeMillis(); memberRef.updateChildren(mapOf("planId" to plan.id, "planLabel" to plan.label, "planPrice" to plan.priceKsh, "planDuration" to plan.durationMonths, "membershipStart" to newStart, "membershipExpiry" to newExpiry, "paymentStatus" to "paid", "lastPaymentId" to paymentId, "lastPaymentAt" to now)).await(); memberRef.child("payments").child(paymentId).setValue(mapOf("paymentId" to paymentId, "type" to "membership_renewal", "planId" to plan.id, "planLabel" to plan.label, "amountKsh" to plan.priceKsh, "status" to "paid", "method" to "demo_mpesa", "paidAt" to now, "membershipStart" to newStart, "membershipExpiry" to newExpiry)).await(); refreshFromFirebase(); onComplete(true) } catch (e: Exception) { renewalError = e.message ?: "Renewal failed"; onComplete(false) } finally { isRenewing = false } }
    }

    private fun persist() { val member = memberForm ?: return; viewModelScope.launch { store.saveMember(member.fullName, member.phone, member.email, member.dob, member.gender, member.emergencyContact, member.securityQuestion, member.securityAnswer, selectedPlan?.id, selectedPlan?.label, selectedPlan?.priceKsh, selectedPlan?.durationMonths, paymentMethod, paymentStatus, qrCodeValue, memberId, membershipStart, membershipExpiry) } }
    fun clearLocalData() { paymentMethod = "mpesa"; paymentStatus = "paid"; renewalError = null; profileSaveError = null }
    fun verifySecurityAnswer(answer: String): Boolean { val form = memberForm ?: return false; return com.qwerty.morningstarfitness.security.hashSecurityAnswer(answer) == form.securityAnswer }
}
