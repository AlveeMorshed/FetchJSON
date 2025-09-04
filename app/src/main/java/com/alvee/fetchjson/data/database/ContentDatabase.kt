package com.alvee.fetchjson.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alvee.fetchjson.data.database.dao.PostFeedDao
import com.alvee.fetchjson.data.model.db_model.PostFeedEntity

@Database(
    entities = [
        PostFeedEntity::class
    ],
    version = 1
)
abstract class ContentDatabase: RoomDatabase() {
    abstract val postFeedDao: PostFeedDao

    companion object {
        const val DATABASE_NAME = "content_database"
    }
}