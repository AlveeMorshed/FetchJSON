package com.alvee.fetchjson.presentation.screens

sealed class Screens(val route: String) {
    object PostFeedScreen : Screens("post_feed_screen")
}
