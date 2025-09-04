package com.alvee.fetchjson.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.alvee.fetchjson.data.model.db_model.PostFeedEntity

@Dao
interface PostFeedDao {
    @Upsert
    suspend fun insertFetchedPosts(posts: List<PostFeedEntity>): List<Long>

    @Query("SELECT * FROM post_feed WHERE userId = :userId ORDER BY postId ASC")
    suspend fun getPostsForUser(userId: Int): List<PostFeedEntity>

    @Query("SELECT * From post_feed WHERE userId=:userId and isFavorite = 1 ORDER BY postId ASC")
    suspend fun getFavoritePosts(userId: Int): List<PostFeedEntity>

//    @Query("UPDATE post_feed SET isFavorite = 1 WHERE postId = :postId")
//    suspend fun markAsFavorite(postId: Int): Int
//
//    @Query("UPDATE post_feed SET isFavorite = 0 WHERE postId = :postId")
//    suspend fun unmarkAsFavorite(postId: Int): Int

    @Query("UPDATE post_feed SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE postId = :postId AND userId = :userId")
    suspend fun toggleFavorite(postId: Int, userId: Int): Int
}