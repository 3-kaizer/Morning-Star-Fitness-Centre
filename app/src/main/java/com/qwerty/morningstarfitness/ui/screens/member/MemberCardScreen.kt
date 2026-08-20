package com.qwerty.morningstarfitness.ui.screens.member

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.QrCodeDisplay
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun MemberCardScreen(fullName: String, memberId: String?, plan: String?, status: String, expiry: String?, qrCode: String?, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column {
                Text("MEMBERSHIP", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Heading("Member card")
            }
        }
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(PulseColors.Accent, PulseColors.AccentLime))).padding(20.dp)) {
            Text("MORNING STAR FITNESS CENTRE", color = PulseColors.Background.copy(alpha = .72f), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp)
            Text(fullName.ifBlank { "Member" }, color = PulseColors.Background, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 8.dp))
            Text(memberId ?: "MEMBER ID PENDING", color = PulseColors.Background.copy(alpha = .8f), fontFamily = FontFamily.Monospace, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
            Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                CardValue("PLAN", plan ?: "—")
                CardValue("STATUS", status)
                CardValue("EXPIRES", expiry ?: "—")
            }
            if (!qrCode.isNullOrBlank()) {
                Spacer(Modifier.height(18.dp))
                Column(Modifier.fillMaxWidth().background(PulseColors.Background, RoundedCornerShape(16.dp)).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    QrCodeDisplay(content = qrCode, sizeDp = 150.dp)
                    Text("Show this QR at gym entry", color = PulseColors.TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 7.dp))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
    }
}

@Composable private fun CardValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = PulseColors.Background.copy(alpha = .65f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text(value, color = PulseColors.Background, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 3.dp))
    }
}
