package com.qwerty.morningstarfitness.ui.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun PasswordEntryScreen(
    memberName: String?,
    securityQuestion: String?,
    isVerifying: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onVerify: (String) -> Unit
) {
    var answer by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PulseColors.TextPrimary)
        }

        Spacer(modifier = Modifier.size(24.dp))
        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = PulseColors.Accent, modifier = Modifier.size(42.dp))
        Spacer(modifier = Modifier.size(16.dp))

        Text("SECURITY CHECK", color = PulseColors.Accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Text("QR not working?", color = PulseColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Text("Answer your security question to record your gym entry without scanning your QR code.", color = PulseColors.TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))

        memberName?.takeIf { it.isNotBlank() }?.let {
            Text("Member: $it", color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 20.dp))
        }

        Spacer(modifier = Modifier.size(24.dp))
        Text(text = securityQuestion?.takeIf { it.isNotBlank() } ?: "Security question unavailable. Please sign in again.", color = PulseColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.size(14.dp))
        OutlinedTextField(value = answer, onValueChange = { answer = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Your answer") }, shape = RoundedCornerShape(14.dp))

        errorMessage?.let { Text(it, color = PulseColors.Error, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp)) }

        Spacer(modifier = Modifier.size(24.dp))
        PrimaryButton(text = if (isVerifying) "VERIFYING..." else "VERIFY & ENTER", onClick = { if (answer.isNotBlank()) onVerify(answer.trim()) }, enabled = answer.isNotBlank() && !isVerifying && !securityQuestion.isNullOrBlank(), modifier = Modifier.fillMaxWidth())

        Text("Your answer is checked against the security information saved for your current member account.", color = PulseColors.TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp))
    }
}
