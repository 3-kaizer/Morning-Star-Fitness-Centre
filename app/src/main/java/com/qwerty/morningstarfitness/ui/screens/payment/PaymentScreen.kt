package com.qwerty.morningstarfitness.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.components.SectionLabel
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun PaymentScreen(
    plan: MembershipPlanModel?,
    phone: String,
    isProcessing: Boolean = false,
    paymentStatus: String? = null,
    receipt: String? = null,
    errorMessage: String? = null,
    isPresentationSandbox: Boolean = false,
    onBack: () -> Unit,
    onPay: () -> Unit,
    onCancel: () -> Unit
) {
    val modeTitle = if (isPresentationSandbox) "PRESENTATION SANDBOX" else "DARaja SANDBOX"
    val modeText = if (isPresentationSandbox) {
        "Presentation-safe simulation. No M-Pesa transaction or PIN is sent. The app records a clearly marked sandbox demo result in Firebase."
    } else {
        "Daraja Sandbox STK Push. A payment request is sent to the configured test backend and the app waits for the Daraja callback before continuing."
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PulseColors.Background).padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp).verticalScroll(rememberScrollState())
                .background(PulseColors.Surface, RoundedCornerShape(20.dp)).padding(24.dp)
        ) {
            BrandMark()
            Heading("M-Pesa payment")
            Spacer(Modifier.height(4.dp))
            Text(modeText, color = PulseColors.TextMuted, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${plan?.label ?: "No plan"} plan", color = PulseColors.TextPrimary, fontSize = 14.sp)
                Text(plan?.let { "KSh ${it.priceKsh}" } ?: "—", color = PulseColors.Accent, fontFamily = FontFamily.Monospace)
            }

            SectionLabel("Payment details")
            Text("Mode", color = PulseColors.TextMuted, fontSize = 11.sp)
            Text(modeTitle, color = PulseColors.Accent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("M-Pesa number", color = PulseColors.TextMuted, fontSize = 11.sp)
            Text(phone.ifBlank { "No phone number" }, color = PulseColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))
            Text("Payment method", color = PulseColors.TextMuted, fontSize = 11.sp)
            Text("M-Pesa", color = PulseColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(18.dp))
            Column(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).padding(16.dp)) {
                Text(if (isPresentationSandbox) "SAFE PRESENTATION DEMO" else "WAIT FOR THE STK PROMPT", color = PulseColors.Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (isPresentationSandbox) "Press the button to demonstrate the payment-confirmed path without sending money or requesting a PIN."
                    else "Enter your M-Pesa PIN only when using the configured Daraja Sandbox test flow. Do not use a personal live transaction for the presentation.",
                    color = PulseColors.TextPrimary,
                    fontSize = 13.sp
                )
            }

            if (paymentStatus != null) {
                Spacer(Modifier.height(12.dp))
                Text("Payment status: ${paymentStatus.uppercase()}", color = if (paymentStatus == "paid") PulseColors.AccentLime else PulseColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (receipt != null) {
                Spacer(Modifier.height(6.dp))
                Text("Receipt: $receipt", color = PulseColors.TextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(errorMessage, color = PulseColors.Error, fontSize = 12.sp)
            }

            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = if (isProcessing) "Processing..." else if (isPresentationSandbox) "RUN SANDBOX PAYMENT" else "PAY WITH M-PESA",
                onClick = onPay,
                enabled = plan != null && phone.isNotBlank() && !isProcessing,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            GhostButton(text = "CANCEL", onClick = onCancel, enabled = !isProcessing, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            GhostButton(text = "Back", onClick = onBack, enabled = !isProcessing, modifier = Modifier.fillMaxWidth())
        }
    }
}
