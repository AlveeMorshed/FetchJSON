package com.alvee.fetchjson.presentation.screens.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvee.fetchjson.utils.DataStoreManager
import com.alvee.fetchjson.utils.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordStrength = getPasswordStrength(password),
            showPasswordStrength = password.isNotEmpty(),
            isPasswordMatch = password.isNotEmpty() && _uiState.value.confirmPassword.isNotEmpty() &&
                    password == _uiState.value.confirmPassword
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            showConfirmPasswordMatch = confirmPassword.isNotEmpty(),
            isPasswordMatch = _uiState.value.password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                    _uiState.value.password == confirmPassword
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _uiState.value

            if( !validateEmail(currentState.email) ){
                _uiState.value = currentState.copy(errorMessage = "Invalid email format")
                return@launch
            }

            if (currentState.password != currentState.confirmPassword) {
                _uiState.value = currentState.copy(errorMessage = "Passwords do not match")
                return@launch
            }
            if (dataStoreManager.isEmailAlreadyRegistered(currentState.email)) {
                _uiState.value = currentState.copy(errorMessage = "Email is already registered.\nPlease login or use a different email.")
                return@launch
            }

            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            val success = dataStoreManager.registerUser(currentState.email, currentState.password)

            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRegistrationSuccessful = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Registration failed. Please try again."
                )
            }
        }
    }

    private fun getPasswordStrength(password: String): PasswordStrength {
        return when {
            password.length < 6 -> PasswordStrength.WEAK
            password.length < 8 -> PasswordStrength.FAIR
            password.length >= 8 && password.any { it.isDigit() } &&
                    password.any { it.isUpperCase() } && password.any { it.isLowerCase() } &&
                    password.any { "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(it) } -> PasswordStrength.STRONG
            password.length >= 8 -> PasswordStrength.GOOD
            else -> PasswordStrength.WEAK
        }
    }
}