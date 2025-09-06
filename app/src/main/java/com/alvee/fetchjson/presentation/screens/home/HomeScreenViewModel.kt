package com.alvee.fetchjson.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvee.fetchjson.utils.NetworkConnectivityObserver
import com.alvee.fetchjson.utils.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _networkStatus = MutableStateFlow(
        if (networkConnectivityObserver.isOnline()) NetworkStatus.Available else NetworkStatus.Unavailable
    )
    val networkStatus = _networkStatus.asStateFlow()

    init {
        viewModelScope.launch {
            networkConnectivityObserver.observe().collect { status ->
                _networkStatus.value = status
            }
        }
    }
    fun updateScreenTitle(screenTitle: String){
        viewModelScope.launch {
            _state.value = _state.value.copy(currentScreen = screenTitle)
        }
    }
}