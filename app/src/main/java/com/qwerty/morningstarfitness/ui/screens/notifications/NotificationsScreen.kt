package com.qwerty.morningstarfitness.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
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
fun NotificationsScreen(
    messages: List<NotificationItem>,
    unreadCount: Int = 0,
    onMarkAllRead: () -> Unit = {},
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
            Column(Modifier.weight(1f)) {
                Text("UPDATES", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Heading("Notifications")
            }
            if (unreadCount > 0) {
                Text("$unreadCount unread", color = PulseColors.Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (unreadCount > 0) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onMarkAllRead) { Text("Mark all as read", color = PulseColors.Accent, fontSize = 12.sp) }
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(messages) { item ->
                val shape = RoundedCornerShape(15.dp)
                Row(
                    Modifier.fillMaxWidth().background(if (item.read) PulseColors.Surface else PulseColors.SurfaceAlt, shape)
                        .border(1.dp, PulseColors.Border, shape).padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(10.dp).background(if (item.read) PulseColors.Border else PulseColors.Accent, CircleShape))
                    Icon(Icons.Default.Notifications, null, tint = PulseColors.Accent, modifier = Modifier.padding(start = 10.dp).size(22.dp))
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

data class NotificationItem(val title: String, val body: String, val time: String, val read: Boolean = false)
