package com.alvee.fetchjson.domain.model

import com.alvee.fetchjson.data.model.api_model.PostItemResponseDto
import com.alvee.fetchjson.data.model.db_model.PostFeedEntity

data class PostItem(
    val userId: Int,
    val postId: Int,
    val title: String,
    val body: String,
    val isFavorite: Boolean = false
)

fun PostItem.toDto(): PostItemResponseDto{
    return PostItemResponseDto(
        userId = userId,
        id = postId,
        title = title,
        body = body
    )
}
fun PostItem.toEntityDto(): PostFeedEntity{
    return PostFeedEntity(
        userId = userId,
        postId = postId,
        title = title,
        body = body,
        isFavorite = isFavorite
    )
}
