package com.alvee.fetchjson.presentation.screens.postfeedscreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

private const val TAG = "PostFeedScreen"

@Composable
fun PostFeedScreen(
    navHostController: NavHostController,
    postFeedViewModel: PostFeedViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by postFeedViewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val isAtLastPost by remember {
        derivedStateOf {
            val lastVisiblePostIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lastVisiblePostIndex == state.postList.lastIndex
        }
    }
    LaunchedEffect(isAtLastPost) {
        Log.d(TAG, "PostFeedScreen: LaunchedEffect Called")
        if (isAtLastPost && state.postList.isNotEmpty()) {
            Log.d(TAG, "PostFeedScreen: ${state.postList.size}")
            postFeedViewModel.getPosts(state.postList.size)
        }
    }

    LazyColumn(
        state = listState
    ) {
        items(state.postList) { post ->
            Box(
                Modifier.padding(32.dp)
            ) {
                Column {
                    Text(text = post.id.toString())
                    Text(text = post.title)
                }

            }

        }
        if (state.isLoading) {
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