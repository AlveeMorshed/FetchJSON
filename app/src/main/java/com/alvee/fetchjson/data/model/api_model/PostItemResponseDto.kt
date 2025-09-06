package com.alvee.fetchjson.data.model.api_model

import com.alvee.fetchjson.domain.model.PostItem

data class PostItemResponseDto(
    val body: String,
    val id: Int,
    val title: String,
)
fun PostItemResponseDto.toDomain(userId: Int, isFavorite: Boolean = false): PostItem{
    return PostItem(
        body = body,
        postId = id,
        title = title,
        userId = userId,
        isFavorite = isFavorite
    )
}