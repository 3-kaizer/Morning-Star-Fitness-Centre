package com.qwerty.morningstarfitness.ui.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.QrCodeDisplay
import com.qwerty.morningstarfitness.ui.theme.PulseColors
import java.util.Calendar

@Composable
fun QrEntryScreen(
    qrCodeValue: String?, fullName: String?, memberId: String?, status: String, membershipExpiry: String?,
    isLoading: Boolean = false, checkInMessage: String? = null, onBack: () -> Unit,
    onContinueToDashboard: () -> Unit = {},
    onPasswordEntry: () -> Unit,
    onRegister: () -> Unit = {}
) {
    Box(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxWidth().widthIn(max = 460.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
                Column(Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("GYM ENTRY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.6.sp)
                    Heading("Show your QR")
                    Text("Present your member QR to the scanner at the gym entrance.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                }
                Box(Modifier.size(42.dp).background(PulseColors.Accent.copy(alpha = .12f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.QrCodeScanner, null, tint = PulseColors.Accent, modifier = Modifier.size(21.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Column(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(20.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(20.dp)).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                when {
                    isLoading -> {
                        Box(Modifier.fillMaxWidth().height(220.dp).background(PulseColors.SurfaceAlt, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PulseColors.Accent, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
                                Text("Loading your member QR...", color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp))
                                Text("Preparing your saved membership details.", color = PulseColors.TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                    qrCodeValue == null || memberId == null -> {
                        Box(Modifier.fillMaxWidth().height(260.dp).background(PulseColors.SurfaceAlt, RoundedCornerShape(14.dp)).border(1.dp, PulseColors.Error.copy(alpha = .45f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(18.dp)) {
                                Icon(Icons.Default.Info, null, tint = PulseColors.Error, modifier = Modifier.size(36.dp))
                                Text("Member Record Not Found", color = PulseColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 10.dp))
                                Text("This account is authenticated but no gym membership was found. Please register to generate your entry QR.", color = PulseColors.TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                                Spacer(Modifier.height(16.dp))
                                com.qwerty.morningstarfitness.ui.components.PrimaryButton("REGISTER NOW", onRegister, Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                GhostButton("Back to Menu", onBack, Modifier.fillMaxWidth())
                            }
                        }
                    }
                    else -> {
                        GymStatusBanner()
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth().background(PulseColors.Accent.copy(alpha = .1f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, null, tint = PulseColors.Accent, modifier = Modifier.size(20.dp))
                            Column(Modifier.padding(start = 9.dp)) {
                                Text("READY FOR SCANNER", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Hold this code in front of the entrance scanner.", color = PulseColors.TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        QrCodeDisplay(content = qrCodeValue, sizeDp = 238.dp)
                        Text("Attendance is recorded automatically when the entrance scan is accepted.", color = PulseColors.TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp))
                    }
                }

                if (qrCodeValue != null && memberId != null) {
                    Spacer(Modifier.height(18.dp))
                    MemberSummary(fullName ?: "Member", memberId, status, membershipExpiry ?: "—")

                    checkInMessage?.let { message ->
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = PulseColors.Accent, modifier = Modifier.size(18.dp))
                            Text(message, color = PulseColors.TextPrimary, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    GhostButton(text = "QR NOT WORKING? USE PASSWORD INSTEAD", onClick = onPasswordEntry, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                    
                    Spacer(Modifier.height(10.dp))
                    com.qwerty.morningstarfitness.ui.components.PrimaryButton(
                        text = "CONTINUE TO DASHBOARD",
                        onClick = onContinueToDashboard,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (qrCodeValue != null && memberId != null) {
                Spacer(Modifier.height(12.dp))
                GhostButton("Back to previous screen", onBack, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun GymStatusBanner() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val isOpen = hour in 6..21 // Open 6 AM to 10 PM (22:00)
    val statusText = if (isOpen) "GYM IS OPEN" else "GYM IS CLOSED"
    val subText = if (isOpen) "Closes at 10:00 PM" else "Opens tomorrow at 6:00 AM"
    val color = if (isOpen) Color(0xFF4CAF50) else PulseColors.Error

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = statusText,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
        Text(
            text = subText,
            color = PulseColors.TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun MemberSummary(name: String, memberId: String, status: String, expiry: String) {
    Column(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(14.dp)).padding(14.dp)) {
        SummaryLine("MEMBER", name)
        SummaryLine("MEMBER ID", memberId)
        SummaryLine("STATUS", status)
        SummaryLine("EXPIRES", expiry)
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = PulseColors.TextMuted, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
        Text(value, color = PulseColors.TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}
