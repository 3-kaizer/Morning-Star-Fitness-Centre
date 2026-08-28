package com.qwerty.morningstarfitness.ui.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun EntryScreen(
    isAuthenticated: Boolean = false,
    memberName: String? = null,
    onLogin: () -> Unit,
    onEnterGym: () -> Unit,
    onLogout: () -> Unit,
    onCreateAccount: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .background(PulseColors.Surface, RoundedCornerShape(22.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandMark()
            Spacer(modifier = Modifier.height(8.dp))
            Heading(if (isAuthenticated) "Welcome back, ${memberName?.split(" ")?.firstOrNull() ?: "Member"}" else "Welcome to Morning Star")
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isAuthenticated) 
                    "You're signed in. Gym entry is just a tap away — show your QR at the desk." 
                    else "Your dashboard lives behind membership. Gym entry is a separate fast lane — just show your QR.",
                color = PulseColors.TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (!isAuthenticated) {
                PrimaryButton(
                    text = "CREATE AN ACCOUNT",
                    onClick = onCreateAccount,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                GhostButton(
                    text = "SIGN IN",
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                PrimaryButton(
                    text = "ENTER THE GYM  ·  SHOW QR",
                    onClick = onEnterGym,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No dashboard detour. Your QR is all the front desk needs.",
                    color = PulseColors.TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                PrimaryButton(
                    text = "ENTER THE GYM",
                    onClick = onEnterGym,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                GhostButton(
                    text = "LOG OUT OF ACCOUNT",
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
