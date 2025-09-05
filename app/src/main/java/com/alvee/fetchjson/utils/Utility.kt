package com.alvee.fetchjson.utils

import android.util.Log

private const val TAG = "Utility"

fun String.filterEmailInput(): String {
    return this.filter { char ->
        char.isLetterOrDigit() || char in "@._-+"
    }
}

fun String.filterPasswordInput(): String {
    return this.filter { char ->
        char.code in 32..126 // Printable ASCII characters
    }
}
fun validatePassword(password: String): String {
    Log.d(TAG, "validatePassword: $password")
    return when {
        password.isEmpty() -> "Password cannot be empty"
        else -> ""
    }
}

fun validateEmail(email: String): Boolean {
    Log.d(TAG, "validateEmail: $email")
    return when {
        email.isEmpty() -> false
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> false
        else -> true
    }
}