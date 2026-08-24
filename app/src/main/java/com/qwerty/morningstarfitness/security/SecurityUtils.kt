package com.qwerty.morningstarfitness.security

import java.security.MessageDigest

fun hashSecurityAnswer(value: String): String {
    val normalized = value.trim().lowercase()
    val bytes = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

fun isSecurityAnswerHash(value: String): Boolean =
    value.length == 64 && value.all { it in "0123456789abcdef" }
