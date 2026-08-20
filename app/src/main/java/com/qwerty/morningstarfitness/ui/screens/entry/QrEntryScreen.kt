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
    Box(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxWidth().widthIn(max = 460.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) }
                Column(Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("GYM ENTRY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.6.sp)
                    Heading("Check in")
                    Text("Show your member QR at the front desk.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
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
                    qrCodeValue == null -> {
                        Box(Modifier.fillMaxWidth().height(220.dp).background(PulseColors.SurfaceAlt, RoundedCornerShape(14.dp)).border(1.dp, PulseColors.Error.copy(alpha = .45f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(18.dp)) {
                                Icon(Icons.Default.Info, null, tint = PulseColors.Error, modifier = Modifier.size(30.dp))
                                Text("QR unavailable", color = PulseColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                                Text("Create or restore your membership before using gym entry.", color = PulseColors.TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
                            }
                        }
                    }
                    else -> {
                        Row(Modifier.fillMaxWidth().background(PulseColors.Accent.copy(alpha = .1f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, null, tint = PulseColors.Accent, modifier = Modifier.size(20.dp))
                            Column(Modifier.padding(start = 9.dp)) {
                                Text("READY FOR SCANNER", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Present this code to identify your membership.", color = PulseColors.TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        QrCodeDisplay(content = qrCodeValue, sizeDp = 238.dp)
                        Text("Keep the QR visible while checking in.", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
                    }
                }

                Spacer(Modifier.height(18.dp))
                MemberSummary(fullName ?: "Member", memberId ?: "Pending", status, membershipExpiry ?: "—")

                checkInMessage?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (message.contains("recorded", true)) Icons.Default.CheckCircle else Icons.Default.Info, null, tint = if (message.contains("recorded", true)) PulseColors.AccentLime else PulseColors.Accent, modifier = Modifier.size(18.dp))
                        Text(message, color = PulseColors.TextPrimary, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(Modifier.height(14.dp))
                PrimaryButton(text = if (isRecording) "RECORDING VISIT..." else "RECORD VISIT", onClick = onRecordCheckIn, enabled = !isRecording && qrCodeValue != null && !isLoading, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(9.dp))
                GhostButton(text = "QR NOT WORKING? USE PASSWORD INSTEAD", onClick = onPasswordEntry, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
            }
            Spacer(Modifier.height(12.dp))
            GhostButton("Back to dashboard", onBack, Modifier.fillMaxWidth())
        }
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
