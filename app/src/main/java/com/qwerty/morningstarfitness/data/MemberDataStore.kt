package com.qwerty.morningstarfitness.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.memberDataStore by preferencesDataStore(name = "morning_star_member")

class MemberDataStore(private val context: Context) {
    private object Keys {
        val onboardingCompleted = stringPreferencesKey("onboarding_completed")
        val authUid = stringPreferencesKey("auth_uid")
        val fullName = stringPreferencesKey("full_name")
        val phone = stringPreferencesKey("phone")
        val email = stringPreferencesKey("email")
        val dob = stringPreferencesKey("dob")
        val gender = stringPreferencesKey("gender")
        val emergencyContact = stringPreferencesKey("emergency_contact")
        val securityQuestion = stringPreferencesKey("security_question")
        val securityAnswer = stringPreferencesKey("security_answer")
        val selectedPlanId = stringPreferencesKey("selected_plan_id")
        val selectedPlanLabel = stringPreferencesKey("selected_plan_label")
        val selectedPlanPrice = stringPreferencesKey("selected_plan_price")
        val selectedPlanDuration = stringPreferencesKey("selected_plan_duration")
        val paymentMethod = stringPreferencesKey("payment_method")
        val paymentStatus = stringPreferencesKey("payment_status")
        val qrCode = stringPreferencesKey("qr_code")
        val memberId = stringPreferencesKey("member_id")
        val membershipStart = stringPreferencesKey("membership_start")
        val membershipExpiry = stringPreferencesKey("membership_expiry")
        val profilePictureUrl = stringPreferencesKey("profile_picture_url")
    }

    suspend fun saveMember(
        authUid: String?,
        fullName: String,
        phone: String,
        email: String,
        dob: String,
        gender: String,
        emergencyContact: String,
        securityQuestion: String?,
        securityAnswer: String?,
        planId: String?,
        planLabel: String?,
        planPrice: Int?,
        planDuration: Int?,
        paymentMethod: String,
        paymentStatus: String,
        qrCode: String?,
        memberId: String? = null,
        membershipStart: String? = null,
        membershipExpiry: String? = null,
        profilePictureUrl: String? = null
    ) {
        context.memberDataStore.edit { p ->
            putIfNotNull(p, Keys.authUid, authUid)
            putIfNotNull(p, Keys.fullName, fullName)
            putIfNotNull(p, Keys.phone, phone)
            putIfNotNull(p, Keys.email, email)
            putIfNotNull(p, Keys.dob, dob)
            putIfNotNull(p, Keys.gender, gender)
            putIfNotNull(p, Keys.emergencyContact, emergencyContact)
            putIfNotNull(p, Keys.securityQuestion, securityQuestion)
            putIfNotNull(p, Keys.securityAnswer, securityAnswer)
            putIfNotNull(p, Keys.selectedPlanId, planId)
            putIfNotNull(p, Keys.selectedPlanLabel, planLabel)
            putIfNotNull(p, Keys.selectedPlanPrice, planPrice?.toString())
            putIfNotNull(p, Keys.selectedPlanDuration, planDuration?.toString())
            putIfNotNull(p, Keys.paymentMethod, paymentMethod)
            putIfNotNull(p, Keys.paymentStatus, paymentStatus)
            putIfNotNull(p, Keys.qrCode, qrCode)
            putIfNotNull(p, Keys.memberId, memberId)
            putIfNotNull(p, Keys.membershipStart, membershipStart)
            putIfNotNull(p, Keys.membershipExpiry, membershipExpiry)
            putIfNotNull(p, Keys.profilePictureUrl, profilePictureUrl)
        }
    }

    suspend fun read(): Map<Preferences.Key<String>, String> {
        val p = context.memberDataStore.data.first()
        return buildMap {
            p[Keys.authUid]?.let { put(Keys.authUid, it) }
            p[Keys.fullName]?.let { put(Keys.fullName, it) }
            p[Keys.phone]?.let { put(Keys.phone, it) }
            p[Keys.email]?.let { put(Keys.email, it) }
            p[Keys.dob]?.let { put(Keys.dob, it) }
            p[Keys.gender]?.let { put(Keys.gender, it) }
            p[Keys.emergencyContact]?.let { put(Keys.emergencyContact, it) }
            p[Keys.securityQuestion]?.let { put(Keys.securityQuestion, it) }
            p[Keys.securityAnswer]?.let { put(Keys.securityAnswer, it) }
            p[Keys.selectedPlanId]?.let { put(Keys.selectedPlanId, it) }
            p[Keys.selectedPlanLabel]?.let { put(Keys.selectedPlanLabel, it) }
            p[Keys.selectedPlanPrice]?.let { put(Keys.selectedPlanPrice, it) }
            p[Keys.selectedPlanDuration]?.let { put(Keys.selectedPlanDuration, it) }
            p[Keys.paymentMethod]?.let { put(Keys.paymentMethod, it) }
            p[Keys.paymentStatus]?.let { put(Keys.paymentStatus, it) }
            p[Keys.qrCode]?.let { put(Keys.qrCode, it) }
            p[Keys.memberId]?.let { put(Keys.memberId, it) }
            p[Keys.membershipStart]?.let { put(Keys.membershipStart, it) }
            p[Keys.membershipExpiry]?.let { put(Keys.membershipExpiry, it) }
            p[Keys.profilePictureUrl]?.let { put(Keys.profilePictureUrl, it) }
        }
    }

    suspend fun clear() {
        context.memberDataStore.edit { p ->
            val onboarding = p[Keys.onboardingCompleted]
            p.clear()
            if (onboarding != null) p[Keys.onboardingCompleted] = onboarding
        }
    }

    suspend fun saveMembershipMetadata(memberId: String, start: String, expiry: String) {
        context.memberDataStore.edit {
            it[Keys.memberId] = memberId
            it[Keys.membershipStart] = start
            it[Keys.membershipExpiry] = expiry
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.memberDataStore.edit { it[Keys.onboardingCompleted] = completed.toString() }
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return context.memberDataStore.data.first()[Keys.onboardingCompleted] == "true"
    }
}

private fun putIfNotNull(preferences: MutablePreferences, key: Preferences.Key<String>, value: String?) {
    if (value == null) preferences.remove(key) else preferences[key] = value
}
