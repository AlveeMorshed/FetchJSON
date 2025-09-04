package com.alvee.fetchjson.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alvee.fetchjson.presentation.screens.Screens
import com.alvee.fetchjson.presentation.screens.postfeedscreen.PostFeedScreen

@Composable
fun Navigation(navController: NavController) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.PostFeedScreen.route
    ) {
        composable(route = Screens.PostFeedScreen.route) {
            PostFeedScreen(
                navHostController = navController,
            )
        }
    }
}