package com.qwerty.morningstarfitness.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
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
    onScanEntry: () -> Unit = {},
    onGetStarted: () -> Unit = onScanEntry,
    onRenewMembership: () -> Unit = {},
    membershipStart: String? = null,
    membershipExpiry: String? = null,
    attendanceCount: Int = 0,
    isLoaded: Boolean = true
) {
    val membershipExpired = membershipExpiry?.let(::isExpired) == true
    val daysLeft = membershipExpiry?.let(::daysUntil)
    val membershipStatus = when { plan == null -> "Not active"; membershipExpired -> "Expired"; else -> "Active" }
    val needsRenewal = plan != null && (membershipExpired || (daysLeft != null && daysLeft <= 5))
    val progress = membershipProgress(membershipStart, membershipExpiry)

    Box(Modifier.fillMaxSize().background(PulseColors.Background).padding(20.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxWidth().widthIn(max = 420.dp).verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
            BrandMark()
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Heading("${greeting()}, $firstName")
                    Text(if (plan != null && !membershipExpired) "Your training space is ready." else "Let's get your membership sorted.", color = PulseColors.TextMuted, fontSize = 13.sp)
                }
                Avatar(firstName)
            }
            Spacer(Modifier.height(4.dp))
            AnimatedVisibility(!isLoaded) { LoadingHero() }
            AnimatedVisibility(isLoaded, enter = fadeIn(), exit = fadeOut()) {
                Column {
                    MembershipHero(
                        plan = plan, status = membershipStatus, daysLeft = daysLeft, expired = membershipExpired,
                        progress = progress,
                        actionText = when { plan == null -> "GET STARTED  ›"; needsRenewal -> "RENEW MEMBERSHIP  ›"; else -> "OPEN CHECK IN  ›" },
                        onAction = when { plan == null -> onGetStarted; needsRenewal -> onRenewMembership; else -> onScanEntry }
                    )
                    if (needsRenewal) {
                        RenewalNotice(daysLeft = daysLeft, expired = membershipExpired, onRenew = onRenewMembership)
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SummaryCard("VISITS", attendanceCount.toString(), Modifier.weight(1f))
                        SummaryCard("DAYS LEFT", when { plan == null -> "—"; membershipExpired -> "0"; daysLeft != null -> daysLeft.toString(); else -> "—" }, Modifier.weight(1f), if (daysLeft != null && daysLeft in 0..5 && !membershipExpired) PulseColors.Accent else PulseColors.TextPrimary)
                        SummaryCard("PLAN", plan?.label ?: "—", Modifier.weight(1f))
                    }
                    SectionLabel("Quick actions")
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            QuickActionTile(Icons.Default.QrCodeScanner, "Check in", onScanEntry, Modifier.weight(1f))
                            QuickActionTile(Icons.Default.ShoppingCart, "Shop", onOpenShop, Modifier.weight(1f))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            QuickActionTile(Icons.Default.History, "History", onOpenAttendance, Modifier.weight(1f))
                            QuickActionTile(Icons.Default.Person, "Profile", onOpenProfile, Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    GhostButton("Log out", onLogout, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable private fun Avatar(firstName: String) = Text(firstName.take(1).uppercase().ifBlank { "M" }, color = PulseColors.Background, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(PulseColors.Accent, CircleShape).padding(horizontal = 15.dp, vertical = 10.dp))

@Composable private fun LoadingHero() = Box(Modifier.fillMaxWidth().padding(top = 18.dp).height(220.dp).background(PulseColors.SurfaceAlt, RoundedCornerShape(18.dp)).border(BorderStroke(1.dp, PulseColors.Border), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PulseColors.Accent, strokeWidth = 3.dp, modifier = Modifier.size(28.dp)) }

@Composable private fun RenewalNotice(daysLeft: Int?, expired: Boolean, onRenew: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 12.dp).background(PulseColors.SurfaceAlt, RoundedCornerShape(14.dp)).border(BorderStroke(1.dp, PulseColors.Border), RoundedCornerShape(14.dp)).padding(14.dp)) {
        Text(if (expired) "Membership expired" else "Membership expires soon", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(if (expired) "Renew your membership to regain gym access." else "${daysLeft ?: 0} day${if ((daysLeft ?: 0) == 1) "" else "s"} remaining. Keep your access uninterrupted.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        Text("RENEW NOW  ›", color = PulseColors.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp).clickable(onClick = onRenew))
    }
}

@Composable private fun MembershipHero(plan: MembershipPlanModel?, status: String, daysLeft: Int?, expired: Boolean, progress: Float?, actionText: String, onAction: () -> Unit) {
    val (statusBg, statusFg) = when { plan == null -> PulseColors.Background.copy(alpha = .14f) to PulseColors.Background.copy(alpha = .72f); expired -> Color(0xFF3A0E06) to Color(0xFFFFD3C4); else -> PulseColors.AccentLime to Color(0xFF17240A) }
    Column(Modifier.fillMaxWidth().padding(top = 18.dp).shadow(20.dp, RoundedCornerShape(18.dp), ambientColor = PulseColors.Accent.copy(alpha = .35f), spotColor = PulseColors.Accent.copy(alpha = .35f)).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(listOf(PulseColors.Accent, Color(0xFFFF9A62)))).padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column { Text("MORNING STAR MEMBERSHIP", color = PulseColors.Background.copy(alpha = .72f), fontSize = 10.sp, fontWeight = FontWeight.Bold); Text(plan?.label ?: "No active plan", color = PulseColors.Background, fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp)) }
            Text(status.uppercase(), color = statusFg, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(statusBg, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 5.dp))
        }
        Text(when { plan == null -> "Register to unlock your membership"; expired -> "Your membership needs renewal"; daysLeft != null && daysLeft <= 5 -> "Only $daysLeft day${if (daysLeft == 1) "" else "s"} left — renew soon"; daysLeft != null -> "$daysLeft days left on your plan"; else -> "Membership active" }, color = PulseColors.Background.copy(alpha = .82f), fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
        if (progress != null) LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(5.dp).clip(RoundedCornerShape(50)), color = PulseColors.Background, trackColor = PulseColors.Background.copy(alpha = .22f))
        Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(plan?.let { "KSh ${it.priceKsh}" } ?: "MSTAR MEMBER", color = PulseColors.Background, fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(actionText, color = PulseColors.Background, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(PulseColors.Background.copy(alpha = .14f), RoundedCornerShape(50)).clickable(onClick = onAction).padding(horizontal = 12.dp, vertical = 8.dp))
        }
    }
}

private fun parseDate(value: String?): Date? = try { if (value.isNullOrBlank()) null else SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value) } catch (_: Exception) { null }
private fun startOfToday(): Date = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
private fun isExpired(expiry: String): Boolean = parseDate(expiry)?.before(startOfToday()) == true
private fun daysUntil(expiry: String): Int? = parseDate(expiry)?.let { TimeUnit.MILLISECONDS.toDays(it.time - startOfToday().time).toInt().coerceAtLeast(0) }
private fun membershipProgress(start: String?, expiry: String?): Float? { val s = parseDate(start) ?: return null; val e = parseDate(expiry) ?: return null; val total = e.time - s.time; if (total <= 0) return null; return ((startOfToday().time - s.time).toFloat() / total).coerceIn(0f, 1f) }
private fun greeting(): String { val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); return when { hour < 12 -> "Good morning"; hour < 17 -> "Good afternoon"; else -> "Good evening" } }

@Composable private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = PulseColors.TextPrimary) = Column(modifier.background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).border(BorderStroke(1.dp, PulseColors.Border), RoundedCornerShape(12.dp)).padding(14.dp)) { Text(label, color = PulseColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.padding(top = 5.dp)) }

@Composable private fun QuickActionTile(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) = Column(modifier.background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).border(BorderStroke(1.dp, PulseColors.Border), RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, label, tint = PulseColors.Accent, modifier = Modifier.size(24.dp)); Spacer(Modifier.height(8.dp)); Text(label, color = PulseColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center) }
