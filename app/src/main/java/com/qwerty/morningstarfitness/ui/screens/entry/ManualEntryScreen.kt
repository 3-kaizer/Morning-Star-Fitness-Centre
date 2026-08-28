package com.qwerty.morningstarfitness.ui.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.MorningStarFitnessTheme
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun ManualEntryScreen(
    onBack: () -> Unit,
    securityQuestion: String? = null,
    onVerifyPassword: (String) -> Boolean = { false },
    onVerifyAnswer: (String) -> Boolean = { false },
    onSuccess: () -> Unit,
    onForgotPassword: (String) -> Unit = {},
    onPasswordSubmitted: ((String, String) -> Unit)? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(horizontal = 22.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PulseColors.TextPrimary)
        }

        Spacer(Modifier.height(18.dp))
        BrandMark()
        Spacer(Modifier.height(36.dp))

        Text(
            "MEMBER LOGIN",
            color = PulseColors.Accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Sign in to your membership",
            color = PulseColors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Use the email and password you registered with.",
            color = PulseColors.TextMuted,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email", color = PulseColors.TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PulseColors.TextPrimary,
                unfocusedTextColor = PulseColors.TextPrimary,
                focusedBorderColor = PulseColors.Accent,
                unfocusedBorderColor = PulseColors.Border
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password", color = PulseColors.TextMuted) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PulseColors.TextPrimary,
                unfocusedTextColor = PulseColors.TextPrimary,
                focusedBorderColor = PulseColors.Accent,
                unfocusedBorderColor = PulseColors.Border
            )
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = error ?: "", color = PulseColors.Error, fontSize = 12.sp)
        }

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "SIGN IN",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "Enter your email and password."
                } else if (onPasswordSubmitted != null) {
                    onPasswordSubmitted(email.trim(), password)
                } else if (onVerifyPassword(password)) {
                    onSuccess()
                } else {
                    error = "Verification failed. Please try again."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        TextButton(
            onClick = { onForgotPassword(email.trim()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot password?", color = PulseColors.Accent)
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E11)
@Composable
fun ManualEntryScreenPreview() {
    MorningStarFitnessTheme {
        ManualEntryScreen(
            onBack = {},
            onSuccess = {}
        )
    }
}
