package com.qwerty.morningstarfitness.models

data class UserModel(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val profilePictureUrl: String? = null,
    val membershipStatus: String = "Inactive",
    val qrCodeValue: String? = null
)
