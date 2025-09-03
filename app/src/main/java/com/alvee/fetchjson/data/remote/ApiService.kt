package com.alvee.fetchjson.data.remote

import com.alvee.fetchjson.data.model.api_model.PostDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("posts")
    suspend fun getPosts(
        @Query("_start") start: Int,
        @Query("_limit") limit: Int
    ): List<PostDto>
}