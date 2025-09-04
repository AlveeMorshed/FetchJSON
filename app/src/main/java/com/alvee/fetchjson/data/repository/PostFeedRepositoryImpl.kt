package com.alvee.fetchjson.data.repository

import com.alvee.fetchjson.data.datasource.LocalDatasource
import com.alvee.fetchjson.data.datasource.RemoteDatasource
import com.alvee.fetchjson.data.model.api_model.toDomain
import com.alvee.fetchjson.data.model.db_model.PostFeedEntity
import com.alvee.fetchjson.data.model.db_model.toDomain
import com.alvee.fetchjson.domain.model.PostItem
import com.alvee.fetchjson.domain.repository.PostFeedRepository
import javax.inject.Inject

class PostFeedRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDatasource,
    private val remoteDatasource: RemoteDatasource
) : PostFeedRepository {
    override suspend fun getPosts(userId: Int, startIndex: Int): List<PostItem> {
        val fetchedPosts = remoteDatasource.getPosts(
            start = startIndex,
            limit = 20
        )
        if (fetchedPosts.isNotEmpty()) {
            localDatasource.insertFetchedPosts(fetchedPosts.map {
                PostFeedEntity(
                    postId = it.id,
                    userId = it.userId,
                    title = it.title,
                    body = it.body,
                )
            })
        }
        return fetchedPosts.map { it.toDomain() }
    }

    override suspend fun getCachedPosts(userId: Int): List<PostItem> {
        val cachedPosts = localDatasource.getPosts(userId)
        return cachedPosts.map { it.toDomain() }
    }
}