package com.qwerty.morningstarfitness.ui.screens.gym

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
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
fun GymStatusScreen(trainerCount: Int, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column {
                Text("TODAY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Heading("Gym status")
            }
        }
        Spacer(Modifier.height(18.dp))
        Column(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(20.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(20.dp)).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime, modifier = Modifier.size(28.dp))
                Column(Modifier.padding(start = 12.dp)) {
                    Text("OPEN", color = PulseColors.AccentLime, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("The gym is ready for your session.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            StatusRow(Icons.Default.AccessTime, "Hours", "6:00 AM – 10:00 PM")
            Spacer(Modifier.height(12.dp))
            StatusRow(Icons.Default.FitnessCenter, "Trainers on duty", "$trainerCount today")
        }
        Spacer(Modifier.weight(1f))
        GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
    }
}

@Composable
private fun StatusRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(14.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PulseColors.Accent, modifier = Modifier.size(20.dp))
        Column(Modifier.padding(start = 12.dp)) {
            Text(label, color = PulseColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = PulseColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 3.dp))
        }
    }
}
