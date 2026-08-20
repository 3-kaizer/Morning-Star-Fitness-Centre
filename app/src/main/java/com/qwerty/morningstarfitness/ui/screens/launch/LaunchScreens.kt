package com.qwerty.morningstarfitness.ui.screens.launch

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.qwerty.morningstarfitness.R
import kotlinx.coroutines.delay

private val OnboardingBackground = Color(0xFF101010)
private val OnboardingSurface = Color(0xFF181818)
private val OnboardingAccent = Color(0xFFFF7A18)

// User-selected Unsplash photography. 800px is sufficient for the onboarding
// card while reducing the first-download payload compared with 1200px.
private val onboardingPhotos = listOf(
    "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&q=80&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1728486145245-d4cb0c9c3470?w=800&q=80&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1641361777339-0ee075cf3ab4?w=800&q=80&auto=format&fit=crop"
)

private val onboardingFallbacks = listOf(
    R.drawable.onboarding_strength,
    R.drawable.onboarding_membership,
    R.drawable.onboarding_progress
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(700)
        onFinished()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(OnboardingBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MORNING STAR", color = OnboardingAccent, style = MaterialTheme.typography.headlineLarge)
            Text("FITNESS CENTRE", color = Color.White, style = MaterialTheme.typography.titleMedium, letterSpacing = 1.8.sp)
            Text("TRAIN • TRACK • TRANSFORM", color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp, modifier = Modifier.padding(top = 10.dp), letterSpacing = 1.6.sp)
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
    val photoCaptions = listOf("YOUR TRAINING STARTS HERE", "ONE QR • ONE MEMBERSHIP", "KNOW YOUR PROGRESS")
    val context = LocalContext.current
    val imageLoader = remember(context) { OnboardingImageLoader.create(context) }

    // Warm Coil's memory/disk cache immediately so swiping to the next page
    // normally does not wait for a fresh network request.
    LaunchedEffect(imageLoader) {
        OnboardingImageLoader.preload(context, imageLoader, onboardingPhotos)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(OnboardingBackground).padding(horizontal = 22.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("MORNING STAR", color = OnboardingAccent, fontSize = 12.sp, letterSpacing = 1.7.sp)
                Text("FITNESS CENTRE", color = Color.White, fontSize = 10.sp, letterSpacing = 1.2.sp)
            }
            Text("${page + 1} / ${titles.size}", color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
        }

        Spacer(Modifier.height(22.dp))

        Crossfade(targetState = page, animationSpec = tween(280), label = "onboarding") { index ->
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(26.dp))
                        .background(Brush.linearGradient(listOf(OnboardingAccent.copy(alpha = 0.22f), OnboardingSurface, Color(0xFF222222))))
                        .border(1.dp, OnboardingAccent.copy(alpha = 0.28f), RoundedCornerShape(26.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(onboardingPhotos[index])
                            .memoryCacheKey(onboardingPhotos[index])
                            .diskCacheKey(onboardingPhotos[index])
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = "Morning Star Fitness Centre onboarding gym photograph",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(onboardingFallbacks[index]),
                        error = painterResource(onboardingFallbacks[index]),
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.42f))))
                    )
                    Text(photoCaptions[index], color = Color.White, fontSize = 11.sp, letterSpacing = 1.3.sp,
                        modifier = Modifier.align(Alignment.BottomStart).padding(18.dp))
                }

                Text(titles[index], color = Color.White, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 24.dp))
                Text(bodies[index], color = Color.LightGray.copy(alpha = 0.82f), fontSize = 14.sp, lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(titles.size) { index ->
                Box(Modifier.size(if (index == page) 24.dp else 7.dp, 7.dp).clip(RoundedCornerShape(50)).background(if (index == page) OnboardingAccent else Color.White.copy(alpha = 0.18f)))
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { if (page == titles.lastIndex) onFinished() else page++ },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OnboardingAccent, contentColor = OnboardingBackground)
        ) {
            Text(if (page == titles.lastIndex) "GET STARTED" else "NEXT", letterSpacing = 0.7.sp)
            if (page < titles.lastIndex) {
                Spacer(Modifier.size(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }

        if (page < titles.lastIndex) {
            Button(
                onClick = onFinished,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White.copy(alpha = 0.72f)),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) { Text("SKIP", fontSize = 12.sp, letterSpacing = 0.8.sp) }
        }
    }
}
