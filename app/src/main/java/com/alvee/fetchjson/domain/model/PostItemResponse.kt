package com.alvee.fetchjson.domain.model

import com.alvee.fetchjson.data.model.api_model.PostItemResponseDto

data class PostItemResponse(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

fun PostItemResponse.toDto(): PostItemResponseDto{
    return PostItemResponseDto(
        userId = userId,
        id = id,
        title = title,
        body = body
    )
}
