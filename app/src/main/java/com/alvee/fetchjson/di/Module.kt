package com.alvee.fetchjson.di

import android.content.Context
import androidx.room.Room
import com.alvee.fetchjson.data.database.ContentDatabase
import com.alvee.fetchjson.data.database.dao.PostFeedDao
import com.alvee.fetchjson.data.datasource.LocalDatasource
import com.alvee.fetchjson.data.datasource.RemoteDatasource
import com.alvee.fetchjson.data.remote.ApiService
import com.alvee.fetchjson.data.repository.PostFeedRepositoryImpl
import com.alvee.fetchjson.domain.repository.PostFeedRepository
import com.alvee.fetchjson.domain.usecase.GetCachedPostsUseCase
import com.alvee.fetchjson.domain.usecase.GetPostsUseCase
import com.alvee.fetchjson.domain.usecase.ToggleFavoriteUseCase
import com.alvee.fetchjson.utils.Constants.BASE_URL
import com.alvee.fetchjson.utils.DataStoreManager
import com.alvee.fetchjson.utils.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager.getInstance(context)
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

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

    @Provides
    @Singleton
    fun provideRemoteDatasource(apiService: ApiService): RemoteDatasource {
        return RemoteDatasource(apiService = apiService)
    }

    @Provides
    @Singleton
    fun provideLocalDatasource(postFeedDao: PostFeedDao): LocalDatasource {
        return LocalDatasource(postFeedDao = postFeedDao)
    }

    @Provides
    @Singleton
    fun providePostFeedRepository(
        remoteDatasource: RemoteDatasource,
        localDatasource: LocalDatasource,
        dataStoreManager: DataStoreManager
    ): PostFeedRepository {
        return PostFeedRepositoryImpl(
            remoteDatasource = remoteDatasource,
            localDatasource = localDatasource,
            dataStoreManager = dataStoreManager
        )
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