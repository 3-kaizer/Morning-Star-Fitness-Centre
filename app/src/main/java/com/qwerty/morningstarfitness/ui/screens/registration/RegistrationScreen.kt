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
    val securityQuestion: String = "",
    val securityAnswer: String = "",
    val profilePictureUrl: String? = null
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
        if (form.email.isBlank()) newErrors["email"] = "Required"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(form.email).matches()) newErrors["email"] = "Invalid email format"
        if (form.dob.isBlank()) newErrors["dob"] = "Required"
        if (form.gender.isBlank()) newErrors["gender"] = "Select gender"
        if (form.password.isBlank()) newErrors["password"] = "Required"
        else if (form.password.length < 6) newErrors["password"] = "Password must be at least 6 characters"
        if (form.confirmPassword.isBlank()) newErrors["confirmPassword"] = "Required"
        else if (form.confirmPassword != form.password) newErrors["confirmPassword"] = "Passwords do not match"
        if (!agreedToTerms) newErrors["terms"] = "You must agree to continue"
        if (form.securityQuestion.isBlank()) newErrors["securityQuestion"] = "Choose a question"
        if (form.securityAnswer.isBlank()) newErrors["securityAnswer"] = "Required"
        errors = newErrors
        return newErrors.isEmpty()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PulseColors.Background).padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp).verticalScroll(rememberScrollState()).background(PulseColors.Surface, RoundedCornerShape(20.dp)).padding(24.dp)
        ) {
            BrandMark()
            Heading("Join the gym")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Create your member account to get started.", color = PulseColors.TextMuted, fontSize = 14.sp)
            SectionLabel("Personal information")
            FormField("Full name", form.fullName, { form = form.copy(fullName = it) }, errors["fullName"])
            FormField("Phone number", form.phone, { form = form.copy(phone = it) }, errors["phone"], keyboardType = KeyboardType.Phone)
            FormField("Email address", form.email, { form = form.copy(email = it) }, errors["email"], keyboardType = KeyboardType.Email)
            FormField("Date of birth (DD/MM/YYYY)", form.dob, { form = form.copy(dob = it) }, errors["dob"], keyboardType = KeyboardType.Number)
            DropdownField("Gender", form.gender.ifBlank { "Select gender" }, listOf("Male", "Female", "Prefer not to say"), { form = form.copy(gender = it) }, errors["gender"])
            FormField("Emergency contact", form.emergencyContact, { form = form.copy(emergencyContact = it) }, keyboardType = KeyboardType.Phone)

            SectionLabel("Account security")
            FormField(
                label = "Password", value = form.password, onValueChange = { form = form.copy(password = it) }, error = errors["password"],
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = PulseColors.TextMuted) } }
            )
            FormField(
                label = "Confirm password", value = form.confirmPassword, onValueChange = { form = form.copy(confirmPassword = it) }, error = errors["confirmPassword"],
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) { Icon(if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = PulseColors.TextMuted) } }
            )

            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 8.dp)) {
                Checkbox(checked = agreedToTerms, onCheckedChange = { agreedToTerms = it }, colors = CheckboxDefaults.colors(checkedColor = PulseColors.Accent, uncheckedColor = PulseColors.Border, checkmarkColor = Color.Black))
                Text("I agree to the gym's terms of membership and liability waiver.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp, end = 4.dp))
            }
            errors["terms"]?.let { Text(it, color = PulseColors.Error, fontSize = 12.sp) }

            SectionLabel("Password recovery")
            Text("Used only if you ever forget your password. It is not used for gym entry.", color = PulseColors.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
            DropdownField("Security question", form.securityQuestion.ifBlank { "Select a question" }, securityQuestions, { form = form.copy(securityQuestion = it) }, errors["securityQuestion"])
            FormField("Your answer", form.securityAnswer, { form = form.copy(securityAnswer = it) }, errors["securityAnswer"])

            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(text = "Continue", onClick = { if (validate()) onContinue(form) })
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your membership plan and payment will be set up next.", color = PulseColors.TextMuted, fontSize = 11.sp, modifier = Modifier.fillMaxWidth())
        }
    }
}
