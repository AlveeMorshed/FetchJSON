package com.alvee.fetchjson.presentation.screens.account

data class AccountUiState(
    val userId: Int = 0,
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
