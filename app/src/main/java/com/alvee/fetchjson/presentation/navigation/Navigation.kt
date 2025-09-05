package com.alvee.fetchjson.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alvee.fetchjson.presentation.screens.Screens
import com.alvee.fetchjson.presentation.screens.SplashScreen
import com.alvee.fetchjson.presentation.screens.loginscreen.LoginScreen
import com.alvee.fetchjson.presentation.screens.postfeedscreen.PostFeedScreen
import com.alvee.fetchjson.presentation.screens.registrationscreen.RegisterScreen

@Composable
fun Navigation(
    navController: NavController,
) {

    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.SplashScreen.route
    ) {
        composable(route = Screens.SplashScreen.route) {
            SplashScreen(
                navHostController = navController
            )
        }
        composable(route = Screens.PostFeedScreen.route) {
            PostFeedScreen(
                navHostController = navController,
            )
        }
        composable(route = Screens.RegisterScreen.route) {
            RegisterScreen(
                navHostController = navController,
                onRegistrationSuccess = {
                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(Screens.RegisterScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(Screens.RegisterScreen.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = Screens.LoginScreen.route) {
            LoginScreen(
                navHostController = navController,
                onLoginSuccess = {
                    navController.navigate(Screens.PostFeedScreen.route) {
                        popUpTo(Screens.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screens.RegisterScreen.route) {
                        popUpTo(Screens.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

    }
}