package com.alvee.fetchjson.data.datasource

import com.alvee.fetchjson.data.model.api_model.PostItemResponseDto
import com.alvee.fetchjson.data.remote.ApiService
import javax.inject.Inject

class RemoteDatasource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPosts(start: Int, limit: Int): List<PostItemResponseDto> {

        val response = apiService.getPosts(start = start, limit = limit)

        return if (response.isSuccessful) {
            response.body()?.toList() ?: emptyList()
        } else {
            emptyList()
        }

    }
}