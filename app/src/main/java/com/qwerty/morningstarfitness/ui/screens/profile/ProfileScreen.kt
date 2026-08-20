package com.qwerty.morningstarfitness.ui.screens.profile

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.components.FormField
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.components.SectionLabel
import com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun ProfileScreen(
    memberForm: MemberFormState?,
    plan: MembershipPlanModel?,
    onBack: () -> Unit,
    onSave: (MemberFormState) -> Unit
) {
    var draft by remember(memberForm) { mutableStateOf(memberForm ?: MemberFormState()) }
    var saved by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(PulseColors.Background).padding(horizontal = 18.dp, vertical = 16.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxWidth().widthIn(max = 460.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PulseColors.TextPrimary)
                }
                Column(Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("ACCOUNT", color = PulseColors.Accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.6.sp)
                    Heading("My profile")
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth().background(PulseColors.Surface, RoundedCornerShape(18.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(18.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(50.dp).background(PulseColors.Accent, CircleShape), contentAlignment = Alignment.Center) {
                    Text(draft.fullName.take(1).uppercase().ifBlank { "M" }, color = PulseColors.Background, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(draft.fullName.ifBlank { "Morning Star Member" }, color = PulseColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(draft.email.ifBlank { "No login email" }, color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("Membership")
            Row(
                Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(15.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(15.dp)).padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CURRENT PLAN", color = PulseColors.TextMuted, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    Text(plan?.label ?: "No active plan", color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                }
                Text(plan?.let { "KSh ${it.priceKsh}" } ?: "—", color = PulseColors.Accent, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            SectionLabel("Personal information")
            Text("Changes are saved to your Firebase member profile.", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 5.dp))
            FormField(label = "Full name", value = draft.fullName, onValueChange = { draft = draft.copy(fullName = it); saved = false })
            FormField(label = "Phone number", value = draft.phone, onValueChange = { draft = draft.copy(phone = it); saved = false }, keyboardType = KeyboardType.Phone)

            Text("Login email", color = PulseColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 10.dp, bottom = 5.dp))
            Text(
                draft.email.ifBlank { "Not set" },
                color = PulseColors.TextPrimary,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(12.dp)).padding(14.dp)
            )

            FormField(label = "Emergency contact", value = draft.emergencyContact, onValueChange = { draft = draft.copy(emergencyContact = it); saved = false }, keyboardType = KeyboardType.Phone)

            Spacer(Modifier.height(16.dp))
            if (saved) {
                Row(Modifier.fillMaxWidth().background(PulseColors.AccentLime.copy(alpha = .12f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = PulseColors.AccentLime, modifier = Modifier.size(18.dp))
                    Text("Your profile was saved and refreshed from Firebase.", color = PulseColors.TextPrimary, fontSize = 11.sp, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(Modifier.height(10.dp))
            }

            PrimaryButton(
                text = if (saved) "SAVED" else "SAVE PROFILE",
                onClick = {
                    saved = false
                    onSave(draft)
                    saved = true
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
