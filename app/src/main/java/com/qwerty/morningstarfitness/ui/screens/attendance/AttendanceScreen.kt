package com.qwerty.morningstarfitness.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qwerty.morningstarfitness.models.AttendanceEntry
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun AttendanceScreen(entries: List<AttendanceEntry>, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(20.dp)) {
        Heading("Attendance")
        Text("Your recent gym visits", color = PulseColors.TextMuted, modifier = Modifier.padding(vertical = 8.dp))
        if (entries.isEmpty()) {
            Text("No attendance records yet.", color = PulseColors.TextMuted, modifier = Modifier.padding(vertical = 24.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(entries) { entry ->
                    Row(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.date, color = PulseColors.TextPrimary)
                        Text(entry.checkIn, color = PulseColors.Accent)
                    }
                }
            }
        }
        GhostButton("Back", onBack, Modifier.fillMaxWidth())
    }
}
