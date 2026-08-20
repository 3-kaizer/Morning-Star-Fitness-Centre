package com.qwerty.morningstarfitness.ui.screens.shop

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.OrderModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors
import com.qwerty.morningstarfitness.viewmodels.ShopViewModel

@Composable
fun OrderSuccessScreen(
    orderId: String,
    viewModel: ShopViewModel,
    onContinue: () -> Unit
) {
    var order by remember { mutableStateOf<OrderModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(orderId) {
        order = viewModel.fetchOrder(orderId)
        isLoading = false
    }

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
                .background(PulseColors.Surface, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandMark()

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(PulseColors.AccentLime.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PulseColors.AccentLime,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Heading("ORDER PLACED")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Order #$orderId",
                color = PulseColors.TextMuted,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            if (isLoading) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = "Loading order details...", color = PulseColors.TextMuted, fontSize = 14.sp)
            } else if (order != null) {
                val o = order!!
                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    SuccessSummaryField("Customer", o.customerName)
                    SuccessSummaryField("Total", "KSh ${o.totalAmount}")
                    SuccessSummaryField("Payment", "M-Pesa Demo — ${o.paymentStatus.uppercase()}")
                    
                    val fulfilmentLabel = if (o.fulfilmentMethod == "delivery") "Delivery" else "Pickup"
                    SuccessSummaryField("Fulfilment", fulfilmentLabel)

                    if (o.fulfilmentMethod == "delivery") {
                        SuccessSummaryField("Location", o.deliveryLocation)
                        SuccessSummaryField("Address", o.deliveryAddress)
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Collect your order from the front desk when it is ready.",
                            color = PulseColors.AccentLime,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = PulseColors.SurfaceAlt)
                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = "Continue Shopping",
                    onClick = onContinue
                )
            } else {
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = "Order details not found.", color = PulseColors.Error, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(text = "Back to Shop", onClick = onContinue)
            }
        }
    }
}

@Composable
fun SuccessSummaryField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = PulseColors.TextMuted, fontSize = 13.sp)
        Text(text = value, color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
}
