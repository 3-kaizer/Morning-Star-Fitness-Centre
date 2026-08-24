package com.qwerty.morningstarfitness.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.theme.DisplayFont
import com.qwerty.morningstarfitness.ui.theme.PulseColors
import com.qwerty.morningstarfitness.utils.generateQrCodeBitmap

@Composable
fun BrandMark() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Canvas(modifier = Modifier.size(width = 28.dp, height = 20.dp)) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.5f)
                lineTo(size.width * 0.25f, size.height * 0.5f)
                lineTo(size.width * 0.34f, size.height * 0.15f)
                lineTo(size.width * 0.46f, size.height * 0.85f)
                lineTo(size.width * 0.57f, size.height * 0.5f)
                lineTo(size.width, size.height * 0.5f)
            }
            drawPath(path = path, color = PulseColors.Accent, style = Stroke(width = 4f))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "MORNING STAR",
            color = PulseColors.TextPrimary,
            fontFamily = DisplayFont,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 18.sp
        )
    }
}

@Composable
fun Heading(text: String) {
    Text(
        text = text,
        color = PulseColors.TextPrimary,
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    )
}

@Composable
fun SectionLabel(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = PulseColors.Accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().height(1.dp),
            color = PulseColors.Border
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = label.uppercase(),
            color = PulseColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(color = PulseColors.TextPrimary, fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PulseColors.SurfaceAlt,
                unfocusedContainerColor = PulseColors.SurfaceAlt,
                focusedBorderColor = PulseColors.Accent,
                unfocusedBorderColor = PulseColors.Border,
                cursorColor = PulseColors.Accent,
                focusedTextColor = PulseColors.TextPrimary,
                unfocusedTextColor = PulseColors.TextPrimary
            ),
            isError = error != null
        )
        error?.let {
            Text(text = it, color = PulseColors.Error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun SelectableOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    valueText: String? = null,
    badgeText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (selected) PulseColors.Accent.copy(alpha = 0.08f) else PulseColors.SurfaceAlt,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                border = BorderStroke(1.dp, if (selected) PulseColors.Accent else PulseColors.Border),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = PulseColors.TextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = badgeText.uppercase(),
                    color = PulseColors.Accent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(PulseColors.Accent.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        if (valueText != null) {
            Text(
                text = valueText,
                color = if (selected) PulseColors.Accent else PulseColors.TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun QrCodeDisplay(
    content: String,
    sizeDp: Dp = 180.dp
) {
    if (content.isBlank()) {
        Box(
            modifier = Modifier
                .size(sizeDp)
                .background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, PulseColors.Border), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "QR Pending",
                color = PulseColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        val bitmap = remember(content) { generateQrCodeBitmap(content, 512) }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Membership QR code",
            modifier = Modifier
                .size(sizeDp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = label.uppercase(),
            color = PulseColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = TextStyle(color = PulseColors.TextPrimary, fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PulseColors.SurfaceAlt,
                    unfocusedContainerColor = PulseColors.SurfaceAlt,
                    focusedBorderColor = PulseColors.Accent,
                    unfocusedBorderColor = PulseColors.Border,
                    focusedTextColor = PulseColors.TextPrimary,
                    unfocusedTextColor = PulseColors.TextPrimary
                ),
                isError = error != null
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = PulseColors.SurfaceAlt
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option, color = PulseColors.TextPrimary, fontSize = 14.sp) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        error?.let {
            Text(text = it, color = PulseColors.Error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PulseColors.Accent,
            contentColor = Color.Black,
            disabledContainerColor = PulseColors.Accent.copy(alpha = 0.3f),
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        )
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, PulseColors.Border),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PulseColors.TextPrimary)
    ) {
        Text(text = text, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
