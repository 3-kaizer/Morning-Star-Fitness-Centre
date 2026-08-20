package com.qwerty.morningstarfitness.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.qwerty.morningstarfitness.models.AttendanceEntry
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun AttendanceScreen(entries: List<AttendanceEntry>, onBack: () -> Unit) {
    val visits = entries.size

    Column(
        Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PulseColors.TextPrimary
                )
            }
            Column {
                Heading("History")
                Text(
                    "Your gym visits",
                    color = PulseColors.TextMuted,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PulseColors.Surface, RoundedCornerShape(16.dp))
                .border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("TOTAL VISITS", color = PulseColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(visits.toString(), color = PulseColors.TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("SOURCE", color = PulseColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Firebase", color = PulseColors.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(14.dp))

        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(PulseColors.SurfaceAlt, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No visits recorded yet.", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold)
                Text(
                    "Your successful gym check-ins will appear here with the exact date and time.",
                    color = PulseColors.TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(entries) { entry ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(PulseColors.Surface, RoundedCornerShape(14.dp))
                            .border(1.dp, PulseColors.Border, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PulseColors.AccentLime
                        )
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(entry.date, color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Check-in time: ${entry.checkIn}", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            entry.checkOut?.let {
                                Text("Check-out time: $it", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Text("RECORDED", color = PulseColors.AccentLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        GhostButton("Back", onBack, Modifier.fillMaxWidth())
    }
}
