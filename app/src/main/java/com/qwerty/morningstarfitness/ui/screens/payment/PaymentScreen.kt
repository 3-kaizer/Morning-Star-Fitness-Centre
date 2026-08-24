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
import com.qwerty.morningstarfitness.ui.components.SelectableOptionRow
import com.qwerty.morningstarfitness.ui.theme.PulseColors

private data class PaymentOption(val id: String, val label: String)

private val paymentOptions = listOf(
    PaymentOption("mpesa", "M-Pesa (Demo Simulator)"),
    PaymentOption("mastercard", "Mastercard (Demo Simulator)")
)

@Composable
fun PaymentScreen(
    plan: MembershipPlanModel?,
    paymentMethod: String,
    isProcessing: Boolean = false,
    errorMessage: String? = null,
    onMethodChange: (String) -> Unit,
    onBack: () -> Unit,
    onSimulateSuccess: () -> Unit,
    onSimulateCancel: () -> Unit
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
                .widthIn(max = 420.dp)
                .verticalScroll(rememberScrollState())
                .background(PulseColors.Surface, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            BrandMark()
            Heading("Membership payment")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "This is a presentation build. All payments are simulated for demonstration purposes.",
                color = PulseColors.TextMuted,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${plan?.label ?: "No plan"} plan",
                    color = PulseColors.TextPrimary,
                    fontSize = 14.sp
                )
                Text(
                    text = plan?.let { "KSh ${it.priceKsh}" } ?: "—",
                    color = PulseColors.Accent,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }

            SectionLabel("Select method")
            paymentOptions.forEach { option ->
                SelectableOptionRow(
                    label = option.label,
                    selected = paymentMethod == option.id,
                    onClick = { if (!isProcessing) onMethodChange(option.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DEMO PAYMENT BOX
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                if (paymentMethod == "mpesa") {
                    Text(
                        text = "M-PESA SANDBOX — DEMO",
                        color = PulseColors.Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "PayBill: 123456", color = PulseColors.TextPrimary, fontSize = 13.sp)
                    Text(text = "Account: MORNINGSTAR-DEMO", color = PulseColors.TextPrimary, fontSize = 13.sp)
                    Text(
                        text = "Amount: KSh ${plan?.priceKsh ?: 0}",
                        color = PulseColors.TextPrimary,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = "MASTERCARD — DEMO",
                        color = PulseColors.Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Card: **** **** **** 4242", color = PulseColors.TextPrimary, fontSize = 13.sp)
                    Text(text = "Expiry: 12/30", color = PulseColors.TextPrimary, fontSize = 13.sp)
                    Text(text = "CVV: ***", color = PulseColors.TextPrimary, fontSize = 13.sp)
                    Text(
                        text = "Amount: KSh ${plan?.priceKsh ?: 0}",
                        color = PulseColors.TextPrimary,
                        fontSize = 13.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No real money is transferred in this presentation build.",
                    color = PulseColors.TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = PulseColors.Error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (isProcessing) "Processing..." else "SIMULATE PAYMENT",
                onClick = onSimulateSuccess,
                enabled = plan != null && !isProcessing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            GhostButton(
                text = "CANCEL PAYMENT",
                onClick = onSimulateCancel,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            GhostButton(
                text = "Back",
                onClick = onBack,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
