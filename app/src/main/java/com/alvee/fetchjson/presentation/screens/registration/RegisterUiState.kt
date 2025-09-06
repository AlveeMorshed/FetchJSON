package com.alvee.fetchjson.presentation.screens.registration

import androidx.compose.ui.graphics.Color

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK,
    val isPasswordMatch: Boolean = false,
    val showPasswordStrength: Boolean = false,
    val showConfirmPasswordMatch: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

enum class PasswordStrength(val message: String, val color: Color, val progress: Float) {
    WEAK("Weak", Color.Red, 0.25f),
    FAIR("Fair", Color(0xFFFF9800), 0.5f),
    GOOD("Good", Color(0xFF2196F3), 0.75f),
    STRONG("Strong", Color(0xFF4CAF50), 1.0f)
}
