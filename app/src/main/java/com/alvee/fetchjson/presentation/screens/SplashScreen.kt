package com.alvee.fetchjson.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.alvee.fetchjson.R
import com.alvee.fetchjson.utils.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager.getInstance(context)
    var isUserLoggedIn by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        val userLoggedIn = withContext(Dispatchers.IO) {
            dataStoreManager.isUserLoggedIn().first()
        }
        isUserLoggedIn = userLoggedIn
        delay(2000)
        navHostController.navigateUp()
        if (isUserLoggedIn) {
            navHostController.navigate(Screens.HomeScreen.route) {
                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
            }
        } else {
            navHostController.navigate(Screens.LoginScreen.route) {
                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fetchjson_logo),
                contentDescription = "FetchJSON Logo",
                modifier = Modifier.fillMaxSize(.45f)
            )
        }
    }
}