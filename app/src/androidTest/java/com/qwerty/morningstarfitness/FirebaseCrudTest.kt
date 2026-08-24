package com.qwerty.morningstarfitness

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState
import com.qwerty.morningstarfitness.viewmodels.AuthViewModel
import com.qwerty.morningstarfitness.viewmodels.MemberViewModel
import kotlinx.coroutines.runBlocking
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

        // Generate unique test data
        val testId = UUID.randomUUID().toString().take(8)
        val testEmail = "test_user_$testId@morningstar.com"
        
        val testForm = MemberFormState(
            fullName = "Test User $testId",
            phone = "0712345678",
            email = testEmail,
            dob = "01/01/1990",
            gender = "Male",
            emergencyContact = "0787654321",
            password = "Password123!",
            confirmPassword = "Password123!"
        )

        val testPlan = MembershipPlanModel(
            id = "gold",
            label = "Gold Plan",
            priceKsh = 5000,
            durationMonths = 12
        )

        // Set up the state
        memberViewModel.updateMemberForm(testForm)
        memberViewModel.updateSelectedPlan(testPlan)

        // Trigger the creation logic
        println("Starting Firebase registration test for: $testEmail")
        val qr = memberViewModel.generateQrCode()
        val success = authViewModel.createUser(testForm, testPlan, qr)
        
        if (success) {
            println("Firebase registration successful.")
            memberViewModel.completePaymentLocally(testPlan)
        } else {
            throw Exception("Failed to register user in Firebase: ${authViewModel.registrationError}")
        }
        
        println("Test completed. User should now appear in Firebase console if configuration is correct.")
    }
}
