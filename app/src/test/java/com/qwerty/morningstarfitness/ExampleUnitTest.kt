package com.qwerty.morningstarfitness

import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState
import com.qwerty.morningstarfitness.viewmodels.RegistrationViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun processPayment_generatesQrCode_afterSuccessfulPayment() {
        val viewModel = RegistrationViewModel()
        viewModel.updateMemberForm(MemberFormState(fullName = "Ada Jane"))
        viewModel.updateSelectedPlan(MembershipPlanModel("monthly", "Monthly", 2000, 1))

        val success = viewModel.processPayment()

        assertTrue(success)
        assertEquals("paid", viewModel.paymentStatus)
        assertNotNull(viewModel.qrCodeValue)
        assertTrue(viewModel.qrCodeValue!!.startsWith("MSF-"))
    }
}