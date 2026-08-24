package com.qwerty.morningstarfitness.ui.screens.trainers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun TrainerDetailScreen(name: String, specialty: String, schedule: String, onBack: () -> Unit) {
    val requested = remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column {
                Text("TRAINER", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Heading(name)
            }
        }
        Spacer(Modifier.height(18.dp))
        Column(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(20.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(20.dp)).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime)
                Text("ON DUTY TODAY", color = PulseColors.AccentLime, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp))
            }
            Text(specialty, color = PulseColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Text("Today's availability", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
            Text(schedule, color = PulseColors.Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
            Spacer(Modifier.height(20.dp))
            if (requested.value) {
                Row(Modifier.fillMaxWidth().background(PulseColors.AccentLime.copy(alpha = .12f), RoundedCornerShape(14.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EventAvailable, null, tint = PulseColors.AccentLime)
                    Text("Session request noted for today's demo.", color = PulseColors.TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(start = 10.dp))
                }
            } else {
                PrimaryButton("REQUEST SESSION", onClick = { requested.value = true })
            }
        }
        Spacer(Modifier.weight(1f))
        GhostButton("Back to trainers", onBack, Modifier.fillMaxWidth())
    }
}
