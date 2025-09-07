package com.alvee.fetchjson.di

import com.alvee.fetchjson.data.database.dao.PostFeedDao
import com.alvee.fetchjson.data.datasource.LocalDatasource
import com.alvee.fetchjson.data.datasource.RemoteDatasource
import com.alvee.fetchjson.data.remote.ApiService
import com.alvee.fetchjson.data.repository.PostFeedRepositoryImpl
import com.alvee.fetchjson.domain.repository.PostFeedRepository
import com.alvee.fetchjson.utils.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

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

}