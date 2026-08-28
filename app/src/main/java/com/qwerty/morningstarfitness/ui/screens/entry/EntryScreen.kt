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
    isAuthenticated: Boolean,
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit,
    onEnterGym: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(PulseColors.Background).padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp)
                .background(PulseColors.Surface, RoundedCornerShape(22.dp)).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandMark()
            Spacer(Modifier.height(8.dp))
            Heading("Welcome to Morning Star")
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Choose an option to continue.",
                color = PulseColors.TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            PrimaryButton("CREATE AN ACCOUNT", onCreateAccount, Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            GhostButton("SIGN IN", onLogin, Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            PrimaryButton("ENTER THE GYM", onEnterGym, Modifier.fillMaxWidth())
        }
    }
}