package com.alvee.fetchjson.presentation.screens

sealed class Screens(val route: String) {
    object SplashScreen : Screens("splash_screen")
    object PostFeedScreen : Screens("post_feed_screen")
    object RegisterScreen : Screens("register_screen")
    object LoginScreen : Screens("login_screen")
}
