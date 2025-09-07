package com.alvee.fetchjson.di

import android.content.Context
import com.alvee.fetchjson.domain.repository.PostFeedRepository
import com.alvee.fetchjson.domain.usecase.GetCachedPostsUseCase
import com.alvee.fetchjson.domain.usecase.GetPostsUseCase
import com.alvee.fetchjson.domain.usecase.ToggleFavoriteUseCase
import com.alvee.fetchjson.utils.DataStoreManager
import com.alvee.fetchjson.utils.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideGetPostsUseCase(
        postFeedRepository: PostFeedRepository
    ): GetPostsUseCase {
        return GetPostsUseCase(
            postFeedRepository = postFeedRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetCachedPostsUseCase(
        postFeedRepository: PostFeedRepository
    ): GetCachedPostsUseCase {
        return GetCachedPostsUseCase(
            postFeedRepository = postFeedRepository
        )
    }

    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(
        postFeedRepository: PostFeedRepository
    ): ToggleFavoriteUseCase {
        return ToggleFavoriteUseCase(
            postFeedRepository = postFeedRepository
        )
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(@ApplicationContext context: Context): NetworkConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }
}