package com.qwerty.morningstarfitness.ui.screens.launch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.qwerty.morningstarfitness.R

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(700)
        onFinished()
    }
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF101010)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("MORNING STAR", color = Color(0xFFFF7A18), style = MaterialTheme.typography.headlineLarge)
        Text("FITNESS CENTRE", color = Color.White, style = MaterialTheme.typography.titleMedium)
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
    val artwork = listOf(R.drawable.onboarding_strength, R.drawable.onboarding_membership, R.drawable.onboarding_progress)
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF101010)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Crossfade(targetState = page, animationSpec = tween(250), label = "onboarding") { index ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(artwork[index]), contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp))
                Text(titles[index], color = Color.White, style = MaterialTheme.typography.headlineMedium)
                Text(bodies[index], color = Color.LightGray, modifier = Modifier.padding(top = 16.dp))
            }
        }
        Row(modifier = Modifier.padding(top = 32.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(titles.size) { index -> Text(if (index == page) "●" else "○", color = Color(0xFFFF7A18)) }
        }
        Button(
            onClick = { if (page == titles.lastIndex) onFinished() else page++ },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) { Text(if (page == titles.lastIndex) "GET STARTED" else "NEXT") }
        if (page < titles.lastIndex) {
            Button(onClick = onFinished, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("SKIP") }
        }
    }
}
