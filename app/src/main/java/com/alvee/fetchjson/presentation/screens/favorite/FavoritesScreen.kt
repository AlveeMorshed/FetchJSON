package com.alvee.fetchjson.presentation.screens.favorite

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alvee.fetchjson.R
import com.alvee.fetchjson.presentation.components.PostCard
import com.alvee.fetchjson.presentation.screens.postfeed.SharedFeedViewModel

@Composable
fun FavoritesScreen(
    sharedFeedViewModel: SharedFeedViewModel
) {
    val state by sharedFeedViewModel.state.collectAsState()
    BackHandler { }

    LaunchedEffect(Unit) {
        sharedFeedViewModel.getFavoritePosts(state.currentUserId)
    }

    if (state.isLoading && state.favoritePostList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    if (state.favoritePostList.isEmpty() && !state.isLoading && state.error.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.no_posts_text))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search posts...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .height(50.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.favoritePostList, key = { it.postId }) { post ->
                PostCard(
                    post = post,
                    onFavoriteClick = { postId ->
                        sharedFeedViewModel.toggleFavorite(postId)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun FavPreview(modifier: Modifier = Modifier) {
    FavoritesScreen(
        hiltViewModel()
    )
}
