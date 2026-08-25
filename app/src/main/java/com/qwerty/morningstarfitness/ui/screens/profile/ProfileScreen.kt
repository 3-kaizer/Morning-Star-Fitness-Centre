package com.qwerty.morningstarfitness.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
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
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            draft = draft.copy(profilePictureUrl = uri.toString())
        }
    }

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

            Spacer(Modifier.height(24.dp))
            
            // Profile Picture Section
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (!draft.profilePictureUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = draft.profilePictureUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(2.dp, PulseColors.Accent, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            Modifier
                                .size(110.dp)
                                .background(PulseColors.SurfaceAlt, CircleShape)
                                .border(1.dp, PulseColors.Border, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                draft.fullName.take(1).uppercase().ifBlank { "M" },
                                color = PulseColors.Accent,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    
                    Box(
                        Modifier
                            .size(34.dp)
                            .background(PulseColors.Accent, CircleShape)
                            .border(2.dp, PulseColors.Background, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = PulseColors.Background, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            
            SectionLabel("Cloudinary Integration")
            Text("Paste a direct link to your Cloudinary image if you prefer.", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 5.dp))
            FormField(
                label = "Profile image URL",
                value = draft.profilePictureUrl ?: "",
                onValueChange = { draft = draft.copy(profilePictureUrl = it) }
            )

            SectionLabel("Personal information")
            Text("Changes are saved to your Firebase member profile.", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 5.dp))
            FormField(label = "Full name", value = draft.fullName, onValueChange = { draft = draft.copy(fullName = it) })
            FormField(label = "Phone number", value = draft.phone, onValueChange = { draft = draft.copy(phone = it) }, keyboardType = KeyboardType.Phone)

            Text("Login email", color = PulseColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 10.dp, bottom = 5.dp))
            Text(
                draft.email.ifBlank { "Not set" },
                color = PulseColors.TextPrimary,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).border(1.dp, PulseColors.Border, RoundedCornerShape(12.dp)).padding(14.dp)
            )

            FormField(label = "Emergency contact", value = draft.emergencyContact, onValueChange = { draft = draft.copy(emergencyContact = it) }, keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = "SAVE PROFILE",
                onClick = { onSave(draft) },
                modifier = Modifier.fillMaxWidth()
            )
            Text("After saving, the app refreshes the member record from Firebase.", color = PulseColors.TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(12.dp))
        }
    }
}
