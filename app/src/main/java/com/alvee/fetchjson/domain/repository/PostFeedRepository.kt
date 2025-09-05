package com.alvee.fetchjson.domain.repository

import com.alvee.fetchjson.domain.model.PostItem

interface PostFeedRepository {
    suspend fun getPosts(userId: Int, startIndex: Int): List<PostItem>
    suspend fun getCachedPosts(userId: Int): List<PostItem>
}