package com.qwerty.morningstarfitness.models

val defaultMembershipPlans = listOf(
    MembershipPlanModel(id = "monthly", label = "Monthly", priceKsh = 2000, durationMonths = 1),
    MembershipPlanModel(id = "quarterly", label = "3 Months", priceKsh = 5000, durationMonths = 3),
    MembershipPlanModel(id = "biannual", label = "6 Months", priceKsh = 9000, durationMonths = 6),
    MembershipPlanModel(id = "annual", label = "Annual", priceKsh = 16000, durationMonths = 12)
)
