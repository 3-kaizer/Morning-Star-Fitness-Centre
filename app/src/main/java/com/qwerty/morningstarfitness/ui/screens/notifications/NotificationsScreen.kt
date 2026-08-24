package com.qwerty.morningstarfitness.ui.screens.notifications

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
import androidx.compose.material.icons.filled.Notifications
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
fun NotificationsScreen(messages: List<NotificationItem>, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column {
                Text("UPDATES", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Heading("Notifications")
            }
        }
        Spacer(Modifier.height(14.dp))
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(messages) { item ->
                Row(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(15.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(15.dp)).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, tint = PulseColors.Accent, modifier = Modifier.size(22.dp))
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(item.title, color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(item.body, color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        Text(item.time, color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
    }
}

data class NotificationItem(val title: String, val body: String, val time: String)
