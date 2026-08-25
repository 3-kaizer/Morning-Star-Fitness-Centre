package com.qwerty.morningstarfitness.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.AttendanceEntry
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    entries: List<AttendanceEntry>,
    monthlyVisits: Int = 0,
    isLoading: Boolean = false,
    loadError: String? = null,
    onRefresh: () -> Unit = {},
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(PulseColors.Background)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary)
                }
                Column(Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("ACTIVITY", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.6.sp)
                    Heading("History")
                }
                Icon(Icons.Default.History, null, tint = PulseColors.Accent, modifier = Modifier.size(24.dp).padding(end = 12.dp))
            }
        },
        containerColor = PulseColors.Background
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                
                AttendanceHero(totalVisits = entries.size, monthlyVisits = monthlyVisits)

                Spacer(Modifier.height(24.dp))

                if (loadError != null) {
                    ErrorState(message = loadError, onRetry = onRefresh)
                } else if (entries.isEmpty() && !isLoading) {
                    EmptyState()
                } else {
                    LazyColumn(
                        Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        if (isLoading && entries.isEmpty()) {
                            item { AttendanceShimmer() }
                        } else {
                            itemsIndexed(entries) { index, entry ->
                                TimelineItem(entry = entry, isLast = index == entries.lastIndex)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                GhostButton("Back to Dashboard", onBack, Modifier.fillMaxWidth().padding(bottom = 16.dp))
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxWidth().fillMaxHeight(0.7f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(64.dp).background(PulseColors.SurfaceAlt, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.History, null, tint = PulseColors.Accent, modifier = Modifier.size(32.dp))
        }
        Text("No visits recorded", color = PulseColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Text("Your gym entry history will appear here once you check in.", color = PulseColors.TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp).padding(horizontal = 32.dp))
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().fillMaxHeight(0.7f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Oops! Something went wrong", color = PulseColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(message, color = PulseColors.TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp).padding(horizontal = 32.dp))
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Retry", onClick = onRetry, modifier = Modifier.width(120.dp))
    }
}
