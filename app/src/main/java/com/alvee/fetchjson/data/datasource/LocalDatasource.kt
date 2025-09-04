package com.alvee.fetchjson.data.datasource

import com.alvee.fetchjson.data.database.dao.PostFeedDao
import com.alvee.fetchjson.data.model.db_model.PostFeedEntity
import javax.inject.Inject

class LocalDatasource @Inject constructor(
    private val postFeedDao: PostFeedDao
) {
    suspend fun insertFetchedPosts(posts: List<PostFeedEntity>): List<Long> {
        return postFeedDao.insertFetchedPosts(posts)
    }

    suspend fun getPosts(userId: Int): List<PostFeedEntity> {
        return postFeedDao.getPostsForUser(userId)
    }
}