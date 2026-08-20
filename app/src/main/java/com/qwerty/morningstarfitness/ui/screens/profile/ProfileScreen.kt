package com.qwerty.morningstarfitness.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
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
                .verticalScroll(rememberScrollState())
                .background(PulseColors.Surface, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PulseColors.TextPrimary)
                }
            }

            BrandMark()
            Heading("My profile")

            Spacer(modifier = Modifier.height(4.dp))
            Text("Update your personal details below.", color = PulseColors.TextMuted, fontSize = 14.sp)

            SectionLabel("Membership")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(plan?.label ?: "No active plan", color = PulseColors.TextPrimary, fontSize = 14.sp)
                Text(plan?.let { "KSh ${it.priceKsh}" } ?: "—", color = PulseColors.Accent, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            }

            SectionLabel("Personal information")
            FormField(
                label = "Full name",
                value = draft.fullName,
                onValueChange = { draft = draft.copy(fullName = it); saved = false }
            )
            FormField(
                label = "Phone number",
                value = draft.phone,
                onValueChange = { draft = draft.copy(phone = it); saved = false },
                keyboardType = KeyboardType.Phone
            )

            // Firebase Authentication owns the login email. Making this field read-only
            // prevents the Firestore member profile from drifting away from Auth.
            Text(
                text = "Login email",
                color = PulseColors.TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Text(
                text = draft.email.ifBlank { "Not set" },
                color = PulseColors.TextPrimary,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseColors.SurfaceAlt, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            )

            FormField(
                label = "Emergency contact",
                value = draft.emergencyContact,
                onValueChange = { draft = draft.copy(emergencyContact = it); saved = false },
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = if (saved) "Saved!" else "Save changes",
                onClick = {
                    onSave(draft)
                    saved = true
                }
            )
        }
    }
}
