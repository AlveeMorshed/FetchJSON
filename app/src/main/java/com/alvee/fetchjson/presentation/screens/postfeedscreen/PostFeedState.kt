package com.alvee.fetchjson.presentation.screens.postfeedscreen

import com.alvee.fetchjson.domain.model.PostItemResponse

data class PostFeedState(
    val isLoading: Boolean = false,
    val error: String = "",
    val endReached: Boolean = false,
    val postList: MutableList<PostItemResponse> = mutableListOf()
)
