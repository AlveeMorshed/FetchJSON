package com.alvee.fetchjson.data.repository

import com.alvee.fetchjson.data.model.api_model.PostItemResponseDto
import com.alvee.fetchjson.data.remote.ApiService
import com.alvee.fetchjson.domain.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {
    override suspend fun getPosts(start: Int, limit: Int): List<PostItemResponseDto> {

        val response = apiService.getPosts(start = start, limit = limit)

        return if (response.isSuccessful) {
            response.body()?.toList() ?: emptyList()
        } else {
            emptyList()
        }

    }
}