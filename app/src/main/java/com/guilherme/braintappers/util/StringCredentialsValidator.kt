package com.guilherme.braintappers.util

import android.util.Patterns
import java.util.regex.Pattern

private const val MIN_PASSWORD_LENGTH = 6
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{4,}$"

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_PASSWORD_LENGTH &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}