package com.alvee.fetchjson.di

import android.content.Context
import androidx.room.Room
import com.alvee.fetchjson.data.database.ContentDatabase
import com.alvee.fetchjson.data.database.dao.PostFeedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providePostFeedDao(contentDatabase: ContentDatabase): PostFeedDao {
        return contentDatabase.postFeedDao
    }

    @Provides
    @Singleton
    fun provideContentDatabase(@ApplicationContext context: Context): ContentDatabase {
        return Room.databaseBuilder(
            context,
            ContentDatabase::class.java,
            ContentDatabase.DATABASE_NAME
        )
            .build()
    }
}