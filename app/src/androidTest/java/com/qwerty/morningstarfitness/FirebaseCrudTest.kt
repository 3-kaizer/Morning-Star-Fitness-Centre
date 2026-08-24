package com.qwerty.morningstarfitness

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState
import com.qwerty.morningstarfitness.viewmodels.AuthViewModel
import com.qwerty.morningstarfitness.viewmodels.MemberViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FirebaseCrudTest {

    @Test
    fun testFirebaseRegistration() = runBlocking {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val authViewModel = AuthViewModel()
        val memberViewModel = MemberViewModel(application)
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance("https://morning-star-6c5e6-default-rtdb.firebaseio.com")

        val testId = UUID.randomUUID().toString().take(8)
        val testEmail = "test_user_$testId@morningstar.com"
        val paymentReference = "TEST-PAY-$testId"
        val mpesaReceipt = "SANDBOX-TEST-$testId"

        val testForm = MemberFormState(
            fullName = "Test User $testId",
            phone = "0712345678",
            email = testEmail,
            dob = "01/01/1990",
            gender = "Male",
            emergencyContact = "0787654321",
            password = "Password123!",
            confirmPassword = "Password123!",
            securityQuestion = "What was the name of your first school?",
            securityAnswer = "Test School"
        )

        val testPlan = MembershipPlanModel(
            id = "gold",
            label = "Gold Plan",
            priceKsh = 5000,
            durationMonths = 12
        )

        memberViewModel.updateMemberForm(testForm)
        memberViewModel.updateSelectedPlan(testPlan)

        try {
            println("Starting Firebase registration test for: $testEmail")
            val qr = memberViewModel.generateQrCode()
            val success = authViewModel.createUser(
                member = testForm,
                plan = testPlan,
                qrCodeValue = qr,
                memberId = "MSFC-TEST-$testId",
                membershipStart = "2026-08-24",
                membershipExpiry = "2027-08-24",
                paymentReference = paymentReference,
                mpesaReceipt = mpesaReceipt
            )

            check(success) { "Firebase registration failed: ${authViewModel.registrationError}" }
            val uid = auth.currentUser?.uid ?: error("Firebase user was not created")
            val memberSnapshot = database.reference.child("members").child(uid).get().await()

            check(memberSnapshot.exists()) { "Member record was not written to Firebase" }
            check(memberSnapshot.child("memberId").getValue(String::class.java) == "MSFC-TEST-$testId")
            check(memberSnapshot.child("payments").child(mpesaReceipt).exists())
            check(memberSnapshot.child("payments").child(mpesaReceipt).child("paymentReference").getValue(String::class.java) == paymentReference)

            println("Firebase registration test passed for: $testEmail")
        } finally {
            val uid = auth.currentUser?.uid
            if (!uid.isNullOrBlank()) {
                runCatching { database.reference.child("members").child(uid).removeValue().await() }
                runCatching { auth.currentUser?.delete()?.await() }
            }
            auth.signOut()
            memberViewModel.clearLocalData()
        }
    }
}
