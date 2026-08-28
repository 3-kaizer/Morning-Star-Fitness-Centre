package com.qwerty.morningstarfitness.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(
                    PulseColors.Surface,
                    RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            BrandMark()

            Heading(
                if (isPresentationSandbox) {
                    "Confirm payment"
                } else {
                    "M-Pesa payment"
                }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "${plan?.label ?: "Membership"} • KSh ${plan?.priceKsh ?: "—"}",
                color = PulseColors.Accent,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "M-Pesa number",
                color = PulseColors.TextMuted,
                fontSize = 11.sp
            )

            Text(
                phone.ifBlank { "No phone number" },
                color = PulseColors.TextPrimary,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                if (isPresentationSandbox) {
                    "Safe presentation payment — no real money will be charged."
                } else {
                    "Confirm the payment on your M-Pesa prompt."
                },
                color = PulseColors.TextMuted,
                fontSize = 13.sp
            )

            if (paymentStatus != null) {
                Spacer(Modifier.height(14.dp))

                Text(
                    "Status: ${paymentStatus.uppercase()}",
                    color = if (paymentStatus == "paid") {
                        PulseColors.AccentLime
                    } else {
                        PulseColors.TextMuted
                    },
                    fontSize = 12.sp
                )
            }

            if (receipt != null) {
                Spacer(Modifier.height(6.dp))

                Text(
                    "Receipt: $receipt",
                    color = PulseColors.TextPrimary,
                    fontSize = 12.sp
                )
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))

                Text(
                    errorMessage,
                    color = PulseColors.Error,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = when {
                    isProcessing -> "Processing..."
                    isPresentationSandbox -> "CONFIRM PAYMENT"
                    else -> "PAY WITH M-PESA"
                },
                onClick = onPay,
                enabled = plan != null &&
                        phone.isNotBlank() &&
                        !isProcessing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            GhostButton(
                "CANCEL",
                onCancel,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            GhostButton(
                "Back",
                onBack,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
