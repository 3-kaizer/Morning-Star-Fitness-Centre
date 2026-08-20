package com.qwerty.morningstarfitness.models

data class MembershipPlanModel(
    val id: String,
    val label: String,
    val priceKsh: Int,
    val durationMonths: Int
)
