package com.qwerty.morningstarfitness.ui.screens.plan

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.models.MembershipPlanModel
import com.qwerty.morningstarfitness.models.defaultMembershipPlans
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.components.SelectableOptionRow
import com.qwerty.morningstarfitness.ui.theme.PulseColors

@Composable
fun PlanScreen(
    onBack: () -> Unit,
    onContinue: (MembershipPlanModel) -> Unit
) {
    var selectedPlanId by remember { mutableStateOf<String?>(null) }

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
                .padding(24.dp)
        ) {
            BrandMark()
            Heading("Choose your plan")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "You can change this later.",
                color = PulseColors.TextMuted,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            defaultMembershipPlans.forEach { plan ->
                SelectableOptionRow(
                    label = plan.label,
                    valueText = "KSh ${plan.priceKsh}",
                    selected = selectedPlanId == plan.id,
                    onClick = { selectedPlanId = plan.id },
                    badgeText = if (plan.id == "quarterly") "Most Popular" else null
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GhostButton(
                    text = "Back",
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "Continue",
                    onClick = {
                        defaultMembershipPlans.find { it.id == selectedPlanId }?.let(onContinue)
                    },
                    enabled = selectedPlanId != null,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
