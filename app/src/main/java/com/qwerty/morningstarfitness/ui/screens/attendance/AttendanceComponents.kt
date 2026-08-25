package com.qwerty.morningstarfitness.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.AttendanceEntry
import com.qwerty.morningstarfitness.ui.theme.PulseColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AttendanceHero(totalVisits: Int, monthlyVisits: Int) {
    val currentMonthName = remember { SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().time).uppercase() }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(PulseColors.Accent, Color(0xFFFF8A65))))
            .padding(24.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text("MONTHLY PROGRESS", color = PulseColors.Background.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp)
                Text("$monthlyVisits visits", color = PulseColors.Background, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }
            Box(
                Modifier
                    .background(PulseColors.Background.copy(alpha = 0.15f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(currentMonthName, color = PulseColors.Background, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        
        Spacer(Modifier.height(20.dp))
        
        Row(
            Modifier
                .fillMaxWidth()
                .background(PulseColors.Background.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("LIFETIME VISITS", color = PulseColors.Background.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                Text(totalVisits.toString(), color = PulseColors.Background, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("RANK", color = PulseColors.Background.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                Text("Regular", color = PulseColors.Background, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TimelineItem(entry: AttendanceEntry, isLast: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
        ) {
            Box(
                Modifier
                    .size(24.dp)
                    .background(PulseColors.Surface, CircleShape)
                    .border(2.dp, PulseColors.AccentLime, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime, modifier = Modifier.size(14.dp))
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(PulseColors.Border)
                )
            }
        }
        
        // Content Column
        Column(
            Modifier
                .weight(1f)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = entry.date.uppercase(),
                color = PulseColors.TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            
            Spacer(Modifier.height(8.dp))
            
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(PulseColors.Surface, RoundedCornerShape(16.dp))
                    .border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Login, null, tint = PulseColors.AccentLime, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Checked in at ${entry.checkIn}",
                        color = PulseColors.TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                if (entry.checkOut != null) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, null, tint = PulseColors.Accent, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Checked out at ${entry.checkOut}",
                            color = PulseColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceShimmer() {
    Column(Modifier.fillMaxWidth()) {
        repeat(3) {
            Row(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Box(Modifier.size(24.dp).background(PulseColors.SurfaceAlt, CircleShape))
                Spacer(Modifier.width(24.dp))
                Box(Modifier.fillMaxWidth().height(80.dp).background(PulseColors.Surface, RoundedCornerShape(16.dp)))
            }
        }
    }
}
