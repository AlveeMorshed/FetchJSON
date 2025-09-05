package com.alvee.fetchjson.data.repository

import android.util.Log
import com.alvee.fetchjson.data.datasource.LocalDatasource
import com.alvee.fetchjson.data.datasource.RemoteDatasource
import com.alvee.fetchjson.data.model.api_model.toDomain
import com.alvee.fetchjson.data.model.db_model.PostFeedEntity
import com.alvee.fetchjson.data.model.db_model.toDomain
import com.alvee.fetchjson.domain.model.PostItem
import com.alvee.fetchjson.domain.repository.PostFeedRepository
import com.alvee.fetchjson.utils.DataStoreManager
import javax.inject.Inject

private const val TAG = "PostFeedRepositoryImpl"

class PostFeedRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDatasource,
    private val remoteDatasource: RemoteDatasource,
    private val dataStoreManager: DataStoreManager
) : PostFeedRepository {
    override suspend fun getPosts(userId: Int, startIndex: Int): List<PostItem> {
        val currentUserId = dataStoreManager.getCurrentUserId()
        Log.d(TAG, "getPosts: $currentUserId")
        val fetchedPosts = remoteDatasource.getPosts(
            start = startIndex,
            limit = 20
        )
        if (fetchedPosts.isNotEmpty() && currentUserId != null) {
            Log.d(TAG, "getPosts: inserting fetchedPosts")
            localDatasource.insertFetchedPosts(fetchedPosts.map {
                PostFeedEntity(
                    postId = it.id,
                    userId = currentUserId,
                    title = it.title,
                    body = it.body,
                )
            })
        }
        return fetchedPosts.map { it.toDomain(userId = currentUserId?:0 ) }
    }

    override suspend fun getCachedPosts(userId: Int): List<PostItem> {
        val cachedPosts = localDatasource.getPosts(userId)
        return cachedPosts.map { it.toDomain() }
    }
}