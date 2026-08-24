package com.qwerty.morningstarfitness.models

data class AttendanceEntry(
    val date: String = "",
    val checkIn: String = "",
    val checkOut: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

val sampleAttendance = listOf(
    AttendanceEntry("Mon, 11 Aug", "6:12 AM", "7:40 AM", timestamp = 1723345920000),
    AttendanceEntry("Sat, 9 Aug", "9:05 AM", "10:30 AM", timestamp = 1723172700000),
    AttendanceEntry("Thu, 7 Aug", "6:20 AM", "7:15 AM", timestamp = 1723000800000),
    AttendanceEntry("Tue, 5 Aug", "5:55 AM", "7:10 AM", timestamp = 1722827700000),
    AttendanceEntry("Sun, 3 Aug", "10:00 AM", "11:20 AM", timestamp = 1722654000000)
)
