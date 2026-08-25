package com.qwerty.morningstarfitness.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun PaymentHistoryScreen(
    entries: List<PaymentHistoryEntry>,
    membershipPlan: String? = null,
    membershipExpiry: String? = null,
    onRenew: () -> Unit = {},
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column {
                Text("ACCOUNT", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Heading("Payment history")
            }
        }
        
        Spacer(Modifier.height(18.dp))
        
        MembershipStatusHeader(membershipPlan, membershipExpiry, onRenew)
        
        Spacer(Modifier.height(24.dp))
        
        Text("TRANSACTION HISTORY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(12.dp))
        
        if (entries.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(PulseColors.Surface, PulseColors.SurfaceAlt)))
                    .border(1.dp, PulseColors.Border, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "LIFETIME SPENT",
                    color = PulseColors.TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Row(
                    Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "KSh ${entries.sumOf { it.amount }}",
                        color = PulseColors.TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "TRANSACTIONS",
                            color = PulseColors.TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = entries.size.toString(),
                            color = PulseColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        if (entries.isEmpty()) {
            Column(Modifier.fillMaxWidth().weight(1f).background(PulseColors.Surface, RoundedCornerShape(18.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(18.dp)).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("No payments yet", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold)
                Text("Completed membership and renewal payments will appear here.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(entries) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PulseColors.Surface, RoundedCornerShape(16.dp))
                            .border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(PulseColors.AccentLime.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime, modifier = Modifier.size(20.dp))
                        }
                        
                        Column(Modifier.weight(1f).padding(start = 14.dp)) {
                            Text(
                                text = item.title,
                                color = PulseColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = item.date,
                                    color = PulseColors.TextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(Modifier.size(3.dp).background(PulseColors.Border, CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.method,
                                    color = PulseColors.Accent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Ref: ${item.reference}",
                                color = PulseColors.TextMuted.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "KSh ${item.amount}",
                                color = PulseColors.TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .background(PulseColors.AccentLime.copy(alpha = 0.15f), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PAID",
                                    color = PulseColors.AccentLime,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
    }
}

@Composable
private fun MembershipStatusHeader(plan: String?, expiry: String?, onRenew: () -> Unit) {
    val expired = expiry?.let { 
        try {
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(it)
            val today = java.util.Calendar.getInstance().apply { set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0) }.time
            date?.before(today) ?: true
        } catch (_: Exception) { true }
    } ?: true

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(PulseColors.Accent, Color(0xFFFF9A62))))
            .padding(20.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text("CURRENT MEMBERSHIP", color = PulseColors.Background.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp)
                Text(plan ?: "No active plan", color = PulseColors.Background, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
            }
            Box(
                Modifier
                    .background(if (expired) Color(0xFF3A0E06) else PulseColors.AccentLime, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (expired) "EXPIRED" else "ACTIVE",
                    color = if (expired) Color(0xFFFFD3C4) else Color(0xFF17240A),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (expired) "EXPIRED ON" else "EXPIRES ON", color = PulseColors.Background.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                Text(expiry ?: "—", color = PulseColors.Background, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            PrimaryButton(
                text = "RENEW",
                onClick = onRenew,
                modifier = Modifier.width(100.dp).height(40.dp)
            )
        }
    }
}

data class PaymentHistoryEntry(val title: String, val amount: Int, val date: String, val reference: String, val method: String = "M-Pesa")
