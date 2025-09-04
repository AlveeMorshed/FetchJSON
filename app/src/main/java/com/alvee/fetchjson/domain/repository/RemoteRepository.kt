package com.alvee.fetchjson.domain.repository

import com.alvee.fetchjson.data.model.api_model.PostItemResponseDto

interface RemoteRepository {
    suspend fun getPosts(start: Int, limit: Int): List<PostItemResponseDto>
}