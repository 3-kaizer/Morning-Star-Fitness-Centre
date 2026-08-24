package com.qwerty.morningstarfitness.ui.screens.registration

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.DropdownField
import com.qwerty.morningstarfitness.ui.components.FormField
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.components.SectionLabel
import com.qwerty.morningstarfitness.ui.theme.PulseColors

private val securityQuestions = listOf(
    "What city were you born in?",
    "What was the name of your first school?",
    "What is your mother's maiden name?",
    "What was the name of your first pet?",
    "What street did you grow up on?"
)

data class MemberFormState(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val dob: String = "",
    val gender: String = "",
    val emergencyContact: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val backupPin: String = "",
    val confirmBackupPin: String = "",
    val securityQuestion: String = "",
    val securityAnswer: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onContinue: (MemberFormState) -> Unit
) {
    var form by remember { mutableStateOf(MemberFormState()) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var errors by remember { mutableStateOf(mapOf<String, String>()) }

    fun validate(): Boolean {
        val newErrors = mutableMapOf<String, String>()

        if (form.fullName.isBlank()) newErrors["fullName"] = "Required"
        if (form.phone.isBlank()) newErrors["phone"] = "Required"
        
        if (form.email.isBlank()) {
            newErrors["email"] = "Required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(form.email).matches()) {
            newErrors["email"] = "Invalid email format"
        }
        
        if (form.dob.isBlank()) newErrors["dob"] = "Required"
        if (form.gender.isBlank()) newErrors["gender"] = "Select gender"

        if (form.password.isBlank()) {
            newErrors["password"] = "Required"
        } else if (form.password.length < 6) {
            newErrors["password"] = "Password must be at least 6 characters"
        }

        if (form.confirmPassword.isBlank()) {
            newErrors["confirmPassword"] = "Required"
        } else if (form.confirmPassword != form.password) {
            newErrors["confirmPassword"] = "Passwords do not match"
        }

        if (!agreedToTerms) newErrors["terms"] = "You must agree to continue"

        if (form.backupPin.isBlank()) {
            newErrors["backupPin"] = "Required"
        } else if (!form.backupPin.matches(Regex("^\\d{4,6}$"))) {
            newErrors["backupPin"] = "4-6 digits only"
        }

        if (form.confirmBackupPin.isBlank()) {
            newErrors["confirmBackupPin"] = "Required"
        } else if (form.confirmBackupPin != form.backupPin) {
            newErrors["confirmBackupPin"] = "PINs do not match"
        }

        if (form.securityQuestion.isBlank()) newErrors["securityQuestion"] = "Choose a question"
        if (form.securityAnswer.isBlank()) newErrors["securityAnswer"] = "Required"

        errors = newErrors
        return newErrors.isEmpty()
    }

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
            BrandMark()
            Heading("Join the gym")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create your member account to get started.",
                color = PulseColors.TextMuted,
                fontSize = 14.sp
            )

            SectionLabel("Personal information")

            FormField(
                label = "Full name",
                value = form.fullName,
                onValueChange = { form = form.copy(fullName = it) },
                error = errors["fullName"]
            )
            FormField(
                label = "Phone number",
                value = form.phone,
                onValueChange = { form = form.copy(phone = it) },
                error = errors["phone"],
                keyboardType = KeyboardType.Phone
            )
            FormField(
                label = "Email address",
                value = form.email,
                onValueChange = { form = form.copy(email = it) },
                error = errors["email"],
                keyboardType = KeyboardType.Email
            )
            FormField(
                label = "Date of birth (DD/MM/YYYY)",
                value = form.dob,
                onValueChange = { form = form.copy(dob = it) },
                error = errors["dob"],
                keyboardType = KeyboardType.Number
            )
            DropdownField(
                label = "Gender",
                selectedOption = form.gender.ifBlank { "Select gender" },
                options = listOf("Male", "Female", "Prefer not to say"),
                onOptionSelected = { form = form.copy(gender = it) },
                error = errors["gender"]
            )
            FormField(
                label = "Emergency contact",
                value = form.emergencyContact,
                onValueChange = { form = form.copy(emergencyContact = it) },
                keyboardType = KeyboardType.Phone
            )

            SectionLabel("Account security")

            FormField(
                label = "Password",
                value = form.password,
                onValueChange = { form = form.copy(password = it) },
                error = errors["password"],
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = PulseColors.TextMuted
                        )
                    }
                }
            )
            FormField(
                label = "Confirm password",
                value = form.confirmPassword,
                onValueChange = { form = form.copy(confirmPassword = it) },
                error = errors["confirmPassword"],
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            imageVector = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = PulseColors.TextMuted
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PulseColors.Accent,
                        uncheckedColor = PulseColors.Border,
                        checkmarkColor = Color.Black
                    )
                )
                Text(
                    text = "I agree to the gym's terms of membership and liability waiver.",
                    color = PulseColors.TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 12.dp, end = 4.dp)
                )
            }

            errors["terms"]?.let {
                Text(text = it, color = PulseColors.Error, fontSize = 12.sp)
            }

            SectionLabel("Entry backup")

            Text(
                text = "In case your QR code ever fails to scan, reception can let you in with this PIN instead.",
                color = PulseColors.TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FormField(
                label = "4-6 digit backup PIN",
                value = form.backupPin,
                onValueChange = { if (it.length <= 6) form = form.copy(backupPin = it) },
                error = errors["backupPin"],
                keyboardType = KeyboardType.NumberPassword
            )
            FormField(
                label = "Confirm PIN",
                value = form.confirmBackupPin,
                onValueChange = { if (it.length <= 6) form = form.copy(confirmBackupPin = it) },
                error = errors["confirmBackupPin"],
                keyboardType = KeyboardType.NumberPassword
            )

            SectionLabel("Password recovery")

            Text(
                text = "Used only if you ever forget your password.",
                color = PulseColors.TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            DropdownField(
                label = "Security question",
                selectedOption = form.securityQuestion.ifBlank { "Select a question" },
                options = securityQuestions,
                onOptionSelected = { form = form.copy(securityQuestion = it) },
                error = errors["securityQuestion"]
            )
            FormField(
                label = "Your answer",
                value = form.securityAnswer,
                onValueChange = { form = form.copy(securityAnswer = it) },
                error = errors["securityAnswer"]
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Continue",
                onClick = { if (validate()) onContinue(form) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your membership plan and payment will be set up next.",
                color = PulseColors.TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
