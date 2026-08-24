package com.qwerty.morningstarfitness.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.SectionLabel
import com.qwerty.morningstarfitness.ui.theme.PulseColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    firstName: String,
    plan: MembershipPlanModel?,
    onLogout: () -> Unit,
    onOpenShop: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenAttendance: () -> Unit = {},
    onOpenTrainers: () -> Unit = {},
    onOpenGymStatus: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenPaymentHistory: () -> Unit = {},
    onOpenMemberCard: () -> Unit = {},
    onScanEntry: () -> Unit = {},
    onGetStarted: () -> Unit = onScanEntry,
    onRenewMembership: () -> Unit = {},
    membershipStart: String? = null,
    membershipExpiry: String? = null,
    attendanceCount: Int = 0,
    trainerCount: Int = 4,
    notificationCount: Int = 2,
    isLoaded: Boolean = true,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val membershipExpired = membershipExpiry?.let(::isExpired) == true
    val daysLeft = membershipExpiry?.let(::daysUntil)
    val membershipStatus = when { plan == null -> "Not active"; membershipExpired -> "Expired"; else -> "Active" }
    val needsRenewal = plan != null && (membershipExpired || (daysLeft != null && daysLeft <= 5))
    val progress = membershipProgress(membershipStart, membershipExpiry)

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.fillMaxWidth().widthIn(max = 460.dp).verticalScroll(rememberScrollState())) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("MEMBER DASHBOARD", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.8.sp)
                        Heading("${greeting()}, $firstName")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.clickable(onClick = onOpenNotifications)) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = PulseColors.TextPrimary, modifier = Modifier.size(24.dp))
                            if (notificationCount > 0) {
                                Box(Modifier.size(8.dp).background(PulseColors.Accent, CircleShape).align(Alignment.TopEnd).border(1.5.dp, PulseColors.Background, CircleShape))
                            }
                        }
                        Box(Modifier.size(42.dp).background(PulseColors.Accent, CircleShape).clickable(onClick = onOpenProfile), contentAlignment = Alignment.Center) {
                            Text(firstName.take(1).uppercase().ifBlank { "M" }, color = PulseColors.Background, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                AnimatedVisibility(!isLoaded) { LoadingHero() }
                AnimatedVisibility(isLoaded, enter = fadeIn(), exit = fadeOut()) {
                    Column {
                        MembershipHero(plan, membershipStatus, daysLeft, membershipExpired, progress)

                        Spacer(Modifier.height(18.dp))
                        PrimaryEnterGymCard(
                            text = if (plan == null) "GET STARTED" else "ENTER THE GYM",
                            subtitle = if (plan == null) "Join our fitness community" else "Show your QR to the scanner",
                            onClick = if (plan == null) onGetStarted else onScanEntry
                        )

                        Spacer(Modifier.height(20.dp))
                        if (needsRenewal) { RenewalNotice(daysLeft, membershipExpired, onRenewMembership); Spacer(Modifier.height(20.dp)) }

                        SectionLabel("Quick Access")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            MetricCard("VISITS", attendanceCount.toString(), Icons.Default.EventAvailable, Modifier.weight(1f).clickable(onClick = onOpenAttendance))
                            MetricCard("DAYS LEFT", when { plan == null -> "—"; membershipExpired -> "0"; daysLeft != null -> daysLeft.toString(); else -> "—" }, Icons.Default.History, Modifier.weight(1f).clickable(onClick = onOpenMemberCard))
                        }

                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionGridItem(Icons.Default.History, "History", onOpenAttendance, Modifier.weight(1f))
                            ActionGridItem(Icons.Default.CreditCard, "Payments", onOpenPaymentHistory, Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionGridItem(Icons.Default.ShoppingCart, "Shop", onOpenShop, Modifier.weight(1f))
                            ActionGridItem(Icons.Default.FitnessCenter, "Trainers", onOpenTrainers, Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(20.dp))
                        Row(Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(15.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(15.dp)).clickable(onClick = onOpenGymStatus).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(38.dp).background(PulseColors.AccentLime.copy(alpha = .15f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.FitnessCenter, null, tint = PulseColors.AccentLime, modifier = Modifier.size(19.dp)) }
                            Column(Modifier.weight(1f).padding(start = 11.dp)) {
                                Text("GYM STATUS", color = PulseColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                Text("$trainerCount trainers on duty today", color = PulseColors.TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
                            }
                            Text("›", color = PulseColors.Accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(24.dp))
                        GhostButton("Log out", onLogout, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable private fun LoadingHero() = Box(Modifier.fillMaxWidth().height(220.dp).background(PulseColors.Surface, RoundedCornerShape(22.dp)).border(BorderStroke(1.dp, PulseColors.Border), RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) { androidx.compose.material3.CircularProgressIndicator(color = PulseColors.Accent, strokeWidth = 3.dp, modifier = Modifier.size(28.dp)) }

@Composable private fun MembershipHero(plan: MembershipPlanModel?, status: String, daysLeft: Int?, expired: Boolean, progress: Float?) {
    val statusBg = if (expired) Color(0xFF3A0E06) else PulseColors.AccentLime
    val statusFg = if (expired) Color(0xFFFFD3C4) else Color(0xFF17240A)
    Column(Modifier.fillMaxWidth().shadow(18.dp, RoundedCornerShape(22.dp)).clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(PulseColors.Accent, Color(0xFFFF9A62)))).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) { Text("MEMBERSHIP", color = PulseColors.Background.copy(alpha = .7f), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp); Text(plan?.label ?: "No active plan", color = PulseColors.Background, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 5.dp)) }
            Text(status.uppercase(), color = statusFg, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.background(statusBg, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 6.dp))
        }
        Text(when { plan == null -> "Join the Morning Star community today."; expired -> "Your membership has expired."; daysLeft != null && daysLeft <= 5 -> "Only $daysLeft day${if (daysLeft == 1) "" else "s"} remaining — renew soon."; daysLeft != null -> "$daysLeft days left on your membership."; else -> "Your membership is active and valid." }, color = PulseColors.Background.copy(alpha = .82f), fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
        if (progress != null) LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(5.dp).clip(RoundedCornerShape(50)), color = PulseColors.Background, trackColor = PulseColors.Background.copy(alpha = .2f))
        Spacer(Modifier.height(10.dp))
        Text(plan?.let { "KSh ${it.priceKsh}" } ?: "MSTAR FITNESS", color = PulseColors.Background, fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PrimaryEnterGymCard(text: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(PulseColors.Accent, RoundedCornerShape(20.dp)).clickable(onClick = onClick).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).background(PulseColors.Background.copy(alpha = .15f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.QrCodeScanner, null, tint = PulseColors.Background, modifier = Modifier.size(24.dp))
        }
        Column(Modifier.weight(1f).padding(start = 16.dp)) {
            Text(text, color = PulseColors.Background, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = PulseColors.Background.copy(alpha = .8f), fontSize = 12.sp)
        }
        Text(if (text == "GET STARTED") "JOIN ›" else "SCAN ›", color = PulseColors.Background, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.background(PulseColors.Background.copy(alpha = .15f), RoundedCornerShape(50)).padding(horizontal = 14.dp, vertical = 8.dp))
    }
}

@Composable
private fun ActionGridItem(icon: ImageVector, title: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.background(PulseColors.Surface, RoundedCornerShape(16.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(14.dp)) {
        Box(Modifier.size(36.dp).background(PulseColors.SurfaceAlt, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = PulseColors.Accent, modifier = Modifier.size(18.dp))
        }
        Text(title, color = PulseColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable private fun RenewalNotice(daysLeft: Int?, expired: Boolean, onRenew: () -> Unit) = Column(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(16.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp)).padding(15.dp)) { Text(if (expired) "Membership expired" else "Membership expires soon", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text(if (expired) "Renew to regain gym access." else "${daysLeft ?: 0} day${if ((daysLeft ?: 0) == 1) "" else "s"} remaining.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)); Text("RENEW NOW  ›", color = PulseColors.Accent, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 9.dp).clickable(onClick = onRenew)) }

@Composable private fun MetricCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) = Column(modifier.background(PulseColors.Surface, RoundedCornerShape(15.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(15.dp)).padding(13.dp)) { Icon(icon, null, tint = PulseColors.Accent, modifier = Modifier.size(18.dp)); Text(label, color = PulseColors.TextMuted, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 9.dp)); Text(value, color = PulseColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, modifier = Modifier.padding(top = 3.dp)) }


private fun parseDate(value: String?): Date? = try { if (value.isNullOrBlank()) null else SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) } catch (_: Exception) { null }
private fun startOfToday(): Date = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
private fun isExpired(expiry: String): Boolean = parseDate(expiry)?.before(startOfToday()) == true
private fun daysUntil(expiry: String): Int? = parseDate(expiry)?.let { TimeUnit.MILLISECONDS.toDays(it.time - startOfToday().time).toInt().coerceAtLeast(0) }
private fun membershipProgress(start: String?, expiry: String?): Float? { val s = parseDate(start) ?: return null; val e = parseDate(expiry) ?: return null; val total = e.time - s.time; if (total <= 0) return null; return ((startOfToday().time - s.time).toFloat() / total).coerceIn(0f, 1f) }
private fun greeting(): String { val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); return when { hour < 12 -> "Good morning"; hour < 17 -> "Good afternoon"; else -> "Good evening" } }
