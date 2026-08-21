package com.qwerty.morningstarfitness.viewmodels

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import com.qwerty.morningstarfitness.security.hashSecurityAnswer
import com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")

    var registrationError by mutableStateOf<String?>(null)
        private set
    var isProcessing by mutableStateOf(false)
        private set

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || password.isBlank()) { onError("Enter your email and password."); return }
        viewModelScope.launch {
            try { auth.signInWithEmailAndPassword(cleanEmail, password).await(); onSuccess() }
            catch (e: Exception) { onError(friendlyAuthError(e)) }
        }
    }

    suspend fun verifyCurrentUserPassword(password: String): Boolean {
        val user = auth.currentUser ?: return false
        val email = user.email ?: return false
        if (password.isBlank()) return false
        return try { user.reauthenticate(EmailAuthProvider.getCredential(email, password)).await(); true } catch (_: Exception) { false }
    }

    suspend fun signInForGymEntry(email: String, password: String): Boolean {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || password.isBlank()) return false
        return try { auth.signInWithEmailAndPassword(cleanEmail, password).await(); true } catch (_: Exception) { false }
    }

    suspend fun createUser(
        member: MemberFormState,
        plan: MembershipPlanModel,
        qrCodeValue: String?,
        memberId: String? = null,
        membershipStart: String? = null,
        membershipExpiry: String? = null,
        paymentReference: String? = null,
        mpesaReceipt: String? = null
    ): Boolean {
        registrationError = null
        isProcessing = true
        var createdUser: com.google.firebase.auth.FirebaseUser? = null
        return try {
            require(member.email.isNotBlank()) { "Email is required." }
            require(member.password.length >= 6) { "Password must be at least 6 characters." }
            require(member.fullName.isNotBlank()) { "Full name is required." }
            require(paymentReference?.isNotBlank() == true) { "Verified payment reference is required." }

            val authResult = auth.createUserWithEmailAndPassword(member.email.trim(), member.password).await()
            createdUser = authResult.user ?: throw Exception("Failed to create user account")
            val uid = createdUser.uid
            val paymentId = mpesaReceipt?.ifBlank { null } ?: paymentReference
            val now = System.currentTimeMillis()

            val memberData = mapOf(
                "uid" to uid,
                "fullName" to member.fullName.trim(),
                "phone" to member.phone.trim(),
                "email" to member.email.trim(),
                "dob" to member.dob,
                "gender" to member.gender,
                "emergencyContact" to member.emergencyContact.trim(),
                "planId" to plan.id,
                "planLabel" to plan.label,
                "planPrice" to plan.priceKsh,
                "planDuration" to plan.durationMonths,
                "qrCode" to qrCodeValue,
                "memberId" to memberId,
                "membershipStart" to membershipStart,
                "membershipExpiry" to membershipExpiry,
                "paymentStatus" to "paid",
                "paymentMethod" to "mpesa_daraja_sandbox",
                "lastPaymentId" to paymentId,
                "lastPaymentAt" to now,
                "securityQuestion" to member.securityQuestion.trim(),
                "securityAnswer" to hashSecurityAnswer(member.securityAnswer),
                "registeredAt" to now
            )

            database.reference.child("members").child(uid).setValue(memberData).await()
            database.reference.child("members").child(uid).child("payments").child(paymentId!!).setValue(
                mapOf(
                    "paymentId" to paymentId,
                    "paymentReference" to paymentReference,
                    "mpesaReceipt" to mpesaReceipt,
                    "type" to "membership_registration",
                    "planId" to plan.id,
                    "planLabel" to plan.label,
                    "amountKsh" to plan.priceKsh,
                    "status" to "paid",
                    "method" to "mpesa_daraja_sandbox",
                    "paidAt" to now,
                    "environment" to "sandbox",
                    "membershipStart" to membershipStart,
                    "membershipExpiry" to membershipExpiry
                )
            ).await()
            true
        } catch (e: Exception) {
            try { createdUser?.delete()?.await() } catch (_: Exception) { }
            registrationError = e.message ?: "Registration failed. Please try again."
            false
        } finally { isProcessing = false }
    }

    fun signOut() = auth.signOut()

    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) { onResult(false, "Enter your account email first."); return }
        viewModelScope.launch {
            try { auth.sendPasswordResetEmail(cleanEmail).await(); onResult(true, "Password reset instructions sent to $cleanEmail.") }
            catch (e: Exception) { onResult(false, e.message ?: "Could not send reset email.") }
        }
    }

    fun currentUser() = auth.currentUser

    private fun friendlyAuthError(error: Exception): String = when (error) {
        is FirebaseAuthInvalidUserException -> "No account was found for that email."
        is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
        else -> error.message ?: "Authentication failed. Please check your details."
    }
}
