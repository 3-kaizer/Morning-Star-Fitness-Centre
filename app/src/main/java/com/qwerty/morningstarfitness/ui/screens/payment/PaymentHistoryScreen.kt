package com.qwerty.morningstarfitness.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun PaymentHistoryScreen(entries: List<PaymentHistoryEntry>, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column {
                Text("ACCOUNT", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Heading("Payment history")
            }
        }
        Spacer(Modifier.height(14.dp))
        if (entries.isEmpty()) {
            Column(Modifier.fillMaxWidth().weight(1f).background(PulseColors.Surface, RoundedCornerShape(18.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(18.dp)).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("No payments yet", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold)
                Text("Completed membership and renewal payments will appear here.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(entries) { item ->
                    Row(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(15.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(15.dp)).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime, modifier = Modifier.size(22.dp))
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(item.title, color = PulseColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(item.date, color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
                            Text(item.reference, color = PulseColors.TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("KSh ${item.amount}", color = PulseColors.Accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("PAID", color = PulseColors.AccentLime, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
    }
}

data class PaymentHistoryEntry(val title: String, val amount: Int, val date: String, val reference: String)
