package com.alvee.fetchjson.presentation.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alvee.fetchjson.R
import com.alvee.fetchjson.presentation.screens.Screens
import com.alvee.fetchjson.presentation.screens.account.AccountScreen
import com.alvee.fetchjson.presentation.screens.favorite.FavoritesScreen
import com.alvee.fetchjson.presentation.screens.postfeed.PostFeedScreen
import com.alvee.fetchjson.presentation.screens.postfeed.SharedFeedViewModel
import com.alvee.fetchjson.ui.theme.RedFirebrick
import com.alvee.fetchjson.utils.Constants
import com.alvee.fetchjson.utils.NetworkStatus

sealed class BottomNavItem(val route: String, val label: String, val icon: Int) {
    object PostFeed :
        BottomNavItem(
            Screens.PostFeedScreen.route,
            Constants.POST_FEED_SCREEN_TITLE,
            R.drawable.outline_dynamic_feed_24
        )

    object Favourites :
        BottomNavItem(
            Screens.FavoritesScreen.route,
            Constants.FAVORITES_SCREEN_TITLE,
            R.drawable.outline_favorite_24
        )

    object Account :
        BottomNavItem(
            Screens.AccountScreen.route,
            Constants.ACCOUNT_SCREEN_TITLE,
            R.drawable.baseline_account_circle_24
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by homeScreenViewModel.state.collectAsState()
    val networkStatus by homeScreenViewModel.networkStatus.collectAsState()
    val bottomNavController = rememberNavController()
    BackHandler { }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = state.currentScreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
            )
        },
        bottomBar = {
            BottomNavigationBar(
                bottomNavController = bottomNavController,
                homeScreenViewModel = homeScreenViewModel
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column {
                if (networkStatus == NetworkStatus.Lost || networkStatus == NetworkStatus.Unavailable) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RedFirebrick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.offline_mode_banner_text),
                            color = Color.White
                        )
                    }
                }
                val sharedFeedViewModel = hiltViewModel<SharedFeedViewModel>()
                NavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavItem.PostFeed.route,
                ) {
                    composable(BottomNavItem.PostFeed.route) {
                        PostFeedScreen(sharedFeedViewModel)
                    }
                    composable(BottomNavItem.Favourites.route) {
                        FavoritesScreen(sharedFeedViewModel)
                    }
                    composable(BottomNavItem.Account.route) {
                        AccountScreen(
                            navHostController = navController,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    bottomNavController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel
) {
    val items = listOf(
        BottomNavItem.PostFeed,
        BottomNavItem.Favourites,
        BottomNavItem.Account
    )

    NavigationBar(
        modifier = Modifier.height(65.dp)
    ) {
        val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    homeScreenViewModel.updateScreenTitle(item.label)
                    bottomNavController.navigate(item.route) {
                        popUpTo(bottomNavController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview(modifier: Modifier = Modifier) {
    HomeScreen(
        navController = rememberNavController()
    )
}



