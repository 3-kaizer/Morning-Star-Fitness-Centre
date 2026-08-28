package com.qwerty.morningstarfitness.ui.screens.success

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.components.QrCodeDisplay
import com.qwerty.morningstarfitness.ui.theme.PulseColors

private val motivationalLines = listOf(
    "Every rep from here counts. Let's go.",
    "Day one starts now. You already did the hard part — showing up.",
    "Strong body, strong mind. Welcome to the journey.",
    "This is the first line of your story. Make it count."
)

@Composable
fun SuccessScreen(
    firstName: String,
    plan: MembershipPlanModel?,
    qrCodeValue: String,
    onContinue: () -> Unit
) {
    val line = motivationalLines[firstName.length % motivationalLines.size]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .background(PulseColors.Surface, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandMark()

            PulseBadge()

            Spacer(modifier = Modifier.height(16.dp))

            Heading("Payment successful")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your first payment for the ${plan?.label ?: "selected"} plan is confirmed. Welcome to the gym, $firstName!",
                color = PulseColors.TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseColors.Accent.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "NEXT STEP",
                    color = PulseColors.Accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Please visit the front desk to confirm your membership and collect your membership details/card.",
                    color = PulseColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            QrCodeDisplay(content = qrCodeValue, sizeDp = 180.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "This is your gym entry code",
                color = PulseColors.TextMuted,
                fontSize = 12.sp
            )
            Text(
                text = "Save a screenshot or find it anytime on your dashboard",
                color = PulseColors.TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseColors.AccentLime.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "\uD83D\uDCAA $line",
                    color = PulseColors.AccentLime,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Go to my dashboard",
                onClick = onContinue
            )

            Spacer(modifier = Modifier.height(8.dp))

            GhostButton(
                text = "Download receipt",
                onClick = { /* Placeholder */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PulseBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "successGlow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, delayMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(PulseColors.AccentLime.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "\uD83C\uDF89", fontSize = 28.sp)
    }
}
