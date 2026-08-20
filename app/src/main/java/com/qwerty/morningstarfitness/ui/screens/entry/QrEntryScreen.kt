package com.qwerty.morningstarfitness.ui.screens.entry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.components.QrCodeDisplay
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun QrEntryScreen(
    qrCodeValue: String?, fullName: String?, memberId: String?, status: String, membershipExpiry: String?,
    isLoading: Boolean = false, isRecording: Boolean, checkInMessage: String?, onBack: () -> Unit,
    onRecordCheckIn: () -> Unit, onPasswordEntry: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PulseColors.Background).padding(20.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxWidth().widthIn(max = 440.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
                Text("GYM ENTRY", color = PulseColors.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }
            Spacer(Modifier.size(16.dp))
            Text("MORNING STAR FITNESS CENTRE", color = PulseColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Heading("Identification")
            Text("Present your member QR to the scanner for fast-lane check-in.", color = PulseColors.TextMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.size(20.dp))
            Column(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(20.dp)).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                when {
                    isLoading -> {
                        Box(Modifier.fillMaxWidth().height(200.dp).background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PulseColors.Accent, strokeWidth = 3.dp, modifier = Modifier.size(30.dp))
                                Text("Loading your member QR...", color = PulseColors.TextMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
                            }
                        }
                    }
                    qrCodeValue == null -> {
                        Box(Modifier.fillMaxWidth().height(200.dp).background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).border(BorderStroke(1.dp, PulseColors.Error.copy(alpha = 0.5f)), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Text("No saved member QR was found on this device.\nCreate or restore your membership first.", color = PulseColors.Error, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                        }
                    }
                    else -> {
                        Row(Modifier.fillMaxWidth().background(PulseColors.Accent.copy(alpha = 0.1f), RoundedCornerShape(12.dp)).padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, null, tint = PulseColors.Accent, modifier = Modifier.size(22.dp))
                            Text("READY FOR SCANNER", color = PulseColors.Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp))
                        }
                        Spacer(Modifier.size(22.dp))
                        QrCodeDisplay(content = qrCodeValue, sizeDp = 232.dp)
                    }
                }
                Spacer(Modifier.size(20.dp))
                InfoRow("Member", fullName ?: "Unregistered")
                InfoRow("Member ID", memberId ?: "Pending")
                InfoRow("Status", status, color = when(status) { "Active" -> PulseColors.AccentLime; "Expired" -> PulseColors.Error; else -> PulseColors.TextMuted })
                InfoRow("Expires", membershipExpiry ?: "—")
                Spacer(Modifier.size(24.dp))
                PrimaryButton(text = if (isRecording) "RECORDING VISIT..." else "RECORD VISIT", onClick = onRecordCheckIn, enabled = !isRecording && qrCodeValue != null && !isLoading)
                Spacer(Modifier.size(12.dp))
                GhostButton(text = "QR NOT WORKING? USE PASSWORD INSTEAD", onClick = onPasswordEntry, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                checkInMessage?.let { message ->
                    Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (message.contains("recorded", true)) Icons.Default.CheckCircle else Icons.Default.Info, null, tint = if (message.contains("recorded", true)) PulseColors.AccentLime else PulseColors.Accent, modifier = Modifier.size(18.dp))
                        Text(message, color = if (message.contains("recorded", true)) PulseColors.AccentLime else PulseColors.TextPrimary, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            Spacer(Modifier.size(14.dp))
            GhostButton(text = "Back to dashboard", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, color: androidx.compose.ui.graphics.Color = PulseColors.TextPrimary) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = PulseColors.TextMuted, fontSize = 12.sp)
        Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
