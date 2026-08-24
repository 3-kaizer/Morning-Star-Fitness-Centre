package com.qwerty.morningstarfitness.ui.screens.attendance

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.AttendanceEntry
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun AttendanceScreen(entries: List<AttendanceEntry>, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary)
            }
            Column(Modifier.weight(1f).padding(start = 4.dp)) {
                Text("ACTIVITY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.6.sp)
                Heading("History")
                Text("Your verified gym visits", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Icon(Icons.Default.History, null, tint = PulseColors.Accent, modifier = Modifier.size(24.dp))
        }

        Spacer(Modifier.padding(top = 12.dp))

        Row(
            Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(18.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(18.dp)).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("TOTAL VISITS", color = PulseColors.TextMuted, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                Text(entries.size.toString(), color = PulseColors.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 3.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("SYNCED", color = PulseColors.TextMuted, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                Text("Firebase", color = PulseColors.AccentLime, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
            }
        }

        Spacer(Modifier.padding(top = 12.dp))

        if (entries.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().weight(1f).background(PulseColors.Surface, RoundedCornerShape(18.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(18.dp)).padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BoxIcon()
                Text("No visits yet", color = PulseColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
                Text("Complete your first check-in and it will appear here with the exact date and time.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(entries) { entry ->
                    Row(
                        Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(16.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp)).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(42.dp).background(PulseColors.AccentLime.copy(alpha = .12f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime, modifier = Modifier.size(20.dp))
                        }
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(entry.date, color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Checked in at ${entry.checkIn}", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                            entry.checkOut?.let { Text("Checked out at $it", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) }
                        }
                        Text("RECORDED", color = PulseColors.AccentLime, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        Spacer(Modifier.padding(top = 10.dp))
        GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
    }
}

@Composable
private fun BoxIcon() {
    Box(
        Modifier.size(48.dp).background(PulseColors.SurfaceAlt, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.History, null, tint = PulseColors.Accent, modifier = Modifier.size(24.dp))
    }
}
