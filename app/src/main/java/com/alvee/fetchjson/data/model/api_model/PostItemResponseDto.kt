package com.alvee.fetchjson.data.model.api_model

import com.alvee.fetchjson.domain.model.PostItemResponse

data class PostItemResponseDto(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int
)
fun PostItemResponseDto.toDomain(): PostItemResponse{
    return PostItemResponse(
        body = body,
        id = id,
        title = title,
        userId = userId
    )
}