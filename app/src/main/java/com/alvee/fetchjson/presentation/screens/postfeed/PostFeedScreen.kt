package com.alvee.fetchjson.presentation.screens.postfeed

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alvee.fetchjson.R
import com.alvee.fetchjson.presentation.screens.Screens
import com.alvee.fetchjson.presentation.screens.home.HomeScreenViewModel
import com.alvee.fetchjson.utils.DataStoreManager
import com.alvee.fetchjson.utils.NetworkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PostFeedScreen"

@Composable
fun PostFeedScreen(
    postFeedViewModel: PostFeedViewModel = hiltViewModel(),
    homeScreenViewModel: HomeScreenViewModel
) {
    val context = LocalContext.current
    val state by postFeedViewModel.state.collectAsState()
    val networkStatus by postFeedViewModel.networkStatus.collectAsState()
    val listState = rememberLazyListState()
    homeScreenViewModel.updateScreenTitle(stringResource(R.string.post_feed_screen_title))

    val isAtLastPost by remember {
        derivedStateOf {
            val lastVisiblePostIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lastVisiblePostIndex == state.postList.lastIndex
        }
    }

    BackHandler {  }

    Log.d(TAG, "PostFeedScreen: NetworkStatus $networkStatus")
    LaunchedEffect(isAtLastPost, networkStatus) {
        Log.d(TAG, "PostFeedScreen: LaunchedEffect Called")
        Log.d(TAG, "PostFeedScreen: ${state.postList.size}")
        when (networkStatus) {
            NetworkStatus.Available -> {
                Log.d(TAG, "Network Available - fetching from remote")
                if (isAtLastPost || state.postList.isEmpty()) postFeedViewModel.getPosts(state.postList.size)
            }

            else -> {
                Log.d(TAG, "Network Unavailable - getting cached posts")
                postFeedViewModel.getCachedPosts(state.currentUserId)
            }
        }
    }
    BackHandler {  }
    if (state.isLoading && state.postList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    if (state.postList.isEmpty() && !state.isLoading && state.error.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.no_posts_text))
        }
    }
    Column {
        LazyColumn(
            state = listState
        ) {
            items(state.postList) { post ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Column {
                        Text(text = post.postId.toString())
                        Text(text = post.title)
                    }
                    Text(
                        text = post.userId.toString(),
                        modifier = Modifier.align(Alignment.TopEnd)
                    )

                }

            }
            if (state.isLoading && state.postList.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}