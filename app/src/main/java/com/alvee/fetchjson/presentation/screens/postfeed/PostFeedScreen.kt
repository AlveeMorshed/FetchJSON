package com.alvee.fetchjson.presentation.screens.postfeed

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alvee.fetchjson.R
import com.alvee.fetchjson.presentation.components.PostCard
import com.alvee.fetchjson.utils.NetworkStatus

private const val TAG = "PostFeedScreen"

@Composable
fun PostFeedScreen(
    sharedFeedViewModel: SharedFeedViewModel,
) {
    val state by sharedFeedViewModel.state.collectAsState()
    val networkStatus by sharedFeedViewModel.networkStatus.collectAsState()
    val listState = rememberLazyListState()

    val displayedPosts = if (state.searchQuery.isEmpty()) state.postList else state.filteredPostList

    val isAtLastPost by remember {
        derivedStateOf {
            val lastVisiblePostIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lastVisiblePostIndex == state.postList.lastIndex
        }
    }

    BackHandler { }

    LaunchedEffect(state.postList) {
        sharedFeedViewModel.updateSearchQuery(state.searchQuery)
    }

    Log.d(TAG, "PostFeedScreen: NetworkStatus $networkStatus")
    LaunchedEffect(isAtLastPost, networkStatus) {
        Log.d(TAG, "PostFeedScreen: LaunchedEffect Called")
        Log.d(TAG, "PostFeedScreen: ${state.postList.size}")
        when (networkStatus) {
            NetworkStatus.Available -> {
                Log.d(TAG, "Network Available - fetching from remote")
                if (isAtLastPost || state.postList.isEmpty()) sharedFeedViewModel.getPosts(state.postList.size)
            }

            else -> {
                Log.d(TAG, "Network Unavailable - getting cached posts")
                sharedFeedViewModel.getCachedPosts(state.currentUserId)
            }
        }
    }
    if (state.isLoading && displayedPosts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    if (displayedPosts.isEmpty() && !state.isLoading && state.error.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (state.searchQuery.isEmpty())
                    stringResource(R.string.no_posts_text)
                else stringResource(
                    R.string.no_posts_found_text
                )
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { sharedFeedViewModel.updateSearchQuery(it) },
            placeholder = { Text(stringResource(R.string.search_bar_placeholder_text)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(52.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(displayedPosts) { post ->
                PostCard(
                    post = post,
                    onFavoriteClick = {
                        sharedFeedViewModel.toggleFavorite(post.postId)
                    }
                )

            }
            if (state.isLoading && displayedPosts.isNotEmpty()) {
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