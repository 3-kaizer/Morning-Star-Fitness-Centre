package com.qwerty.morningstarfitness.ui.screens.launch

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import kotlinx.coroutines.delay
import com.qwerty.morningstarfitness.R

private val OnboardingBackground = Color(0xFF101010)
private val OnboardingSurface = Color(0xFF181818)
private val OnboardingAccent = Color(0xFFFF7A18)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(700)
        onFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MORNING STAR", color = OnboardingAccent, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Text("FITNESS CENTRE", color = Color.White, style = MaterialTheme.typography.titleMedium, letterSpacing = 1.8.sp)
            Text("TRAIN • TRACK • TRANSFORM", color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp), letterSpacing = 1.6.sp)
        }
    }
}

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    val titles = listOf("Train with purpose", "Your membership, always with you", "Track every visit")
    val bodies = listOf(
        "Build strength, consistency, and confidence at Morning Star.",
        "Register once, keep your permanent member QR, and renew when needed.",
        "View attendance, membership status, trainers, and the gym shop from one dashboard."
    )
    val artwork = listOf(
        R.drawable.onboarding_strength,
        R.drawable.onboarding_membership,
        R.drawable.onboarding_progress
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBackground)
            .padding(horizontal = 22.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("MORNING STAR", color = OnboardingAccent, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.7.sp)
                Text("FITNESS CENTRE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            }
            Text("${page + 1} / ${titles.size}", color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(22.dp))

        Crossfade(targetState = page, animationSpec = tween(280), label = "onboarding") { index ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    OnboardingAccent.copy(alpha = 0.22f),
                                    OnboardingSurface,
                                    Color(0xFF222222)
                                )
                            )
                        )
                        .border(1.dp, OnboardingAccent.copy(alpha = 0.28f), RoundedCornerShape(26.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(artwork[index]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.34f)
                                    )
                                )
                            )
                    )
                    Text(
                        when (index) {
                            0 -> "YOUR TRAINING STARTS HERE"
                            1 -> "ONE QR • ONE MEMBERSHIP"
                            else -> "KNOW YOUR PROGRESS"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.3.sp,
                        modifier = Modifier.align(Alignment.BottomStart).padding(18.dp)
                    )
                }

                Text(
                    titles[index],
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(
                    bodies[index],
                    color = Color.LightGray.copy(alpha = 0.82f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(titles.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == page) 24.dp else 7.dp, 7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (index == page) OnboardingAccent else Color.White.copy(alpha = 0.18f))
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { if (page == titles.lastIndex) onFinished() else page++ },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OnboardingAccent, contentColor = OnboardingBackground)
        ) {
            Text(if (page == titles.lastIndex) "GET STARTED" else "NEXT", fontWeight = FontWeight.ExtraBold, letterSpacing = 0.7.sp)
            Spacer(Modifier.size(8.dp))
            if (page < titles.lastIndex) Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }

        if (page < titles.lastIndex) {
            Button(
                onClick = onFinished,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White.copy(alpha = 0.72f)),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Text("SKIP", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
            }
        }
    }
}
