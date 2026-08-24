package com.qwerty.morningstarfitness.ui.screens.trainers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Schedule
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

data class TrainerSummary(val name: String, val specialty: String, val schedule: String, val status: String)

val todayTrainers = listOf(
    TrainerSummary("Alex Mwangi", "Strength & Conditioning", "6:00 AM – 2:00 PM", "Available"),
    TrainerSummary("Brian Kamau", "Weight Loss & HIIT", "10:00 AM – 6:00 PM", "Available"),
    TrainerSummary("Sarah Wanjiku", "Personal Training", "2:00 PM – 9:00 PM", "Available"),
    TrainerSummary("Kevin Otieno", "Functional Fitness", "4:00 PM – 10:00 PM", "Available")
)

@Composable
fun TrainersScreen(onBack: () -> Unit, onTrainerSelected: (TrainerSummary) -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column {
                Text("TODAY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.6.sp)
                Heading("Trainers")
            }
        }
        Text("Meet the coaches working on the gym floor today.", color = PulseColors.TextMuted, fontSize = 13.sp, modifier = Modifier.padding(start = 54.dp, top = 2.dp))
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().background(PulseColors.Accent.copy(alpha = .10f), RoundedCornerShape(16.dp)).border(1.dp, PulseColors.Accent.copy(alpha = .25f), RoundedCornerShape(16.dp)).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(PulseColors.Accent, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.FitnessCenter, null, tint = PulseColors.Background, modifier = Modifier.size(21.dp)) }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("${todayTrainers.size} trainers on duty", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Tap a trainer to view details and request a session.", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
            }
            Text("ON DUTY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(14.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(todayTrainers) { trainer -> TrainerCard(trainer) { onTrainerSelected(trainer) } }
        }
        Spacer(Modifier.height(12.dp))
        GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
    }
}

@Composable
private fun TrainerCard(trainer: TrainerSummary, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(16.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).background(PulseColors.SurfaceAlt, CircleShape), contentAlignment = Alignment.Center) { Text(trainer.name.take(1), color = PulseColors.Accent, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp) }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(trainer.name, color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(trainer.specialty, color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Default.Schedule, null, tint = PulseColors.Accent, modifier = Modifier.size(15.dp))
                Text(trainer.schedule, color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime, modifier = Modifier.size(19.dp))
            Text(trainer.status.uppercase(), color = PulseColors.AccentLime, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
