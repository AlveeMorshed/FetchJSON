package com.alvee.fetchjson.presentation.screens.registrationscreen

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
import kotlinx.coroutines.flow.first

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
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
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
}