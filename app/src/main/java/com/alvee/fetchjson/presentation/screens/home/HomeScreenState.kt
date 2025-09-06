package com.alvee.fetchjson.presentation.screens.home

data class HomeScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val currentScreen: String = "News Feed"
)
