package com.alvee.fetchjson.presentation.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvee.fetchjson.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountScreenViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        loadUserDetails()
    }

    private fun loadUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val email = dataStoreManager.getCurrentUserEmail()
            val userId = dataStoreManager.getCurrentUserId()
            _uiState.value = _uiState.value.copy(
                email = email.toString(),
                userId = userId ?: 0
            )
        }
    }
    fun logOutUser(){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.logoutUser()
            _uiState.value = AccountUiState()
        }
    }

}