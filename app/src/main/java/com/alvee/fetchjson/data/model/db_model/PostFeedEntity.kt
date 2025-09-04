package com.alvee.fetchjson.data.model.db_model

import androidx.room.Entity
import androidx.room.Index
import com.alvee.fetchjson.domain.model.PostItem

@Entity(
    tableName = "post_feed",
    primaryKeys = ["postId", "userId"],
    indices = [Index(value = ["userId", "isFavorite"])]
)
data class PostFeedEntity(
    val userId: Int,
    val postId: Int,
    val title: String,
    val body: String,
    val isFavorite: Boolean = false
)
fun PostFeedEntity.toDomain(): PostItem {
    return PostItem(
        body = body,
        postId = postId,
        title = title,
        userId = userId,
        isFavorite = isFavorite
    )
}
