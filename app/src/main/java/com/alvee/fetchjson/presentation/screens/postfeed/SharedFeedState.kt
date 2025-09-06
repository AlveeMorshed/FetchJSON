package com.alvee.fetchjson.presentation.screens.postfeed

import com.alvee.fetchjson.domain.model.PostItem

data class SharedFeedState(
    val isLoading: Boolean = false,
    val error: String = "",
    val endReached: Boolean = false,
    val currentUserId: Int = 0,
    val postList: MutableList<PostItem> = mutableListOf(),
    val favoritePostList: MutableList<PostItem> = mutableListOf(),
)
