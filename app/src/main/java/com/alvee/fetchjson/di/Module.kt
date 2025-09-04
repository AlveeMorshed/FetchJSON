package com.alvee.fetchjson.di

import android.app.Application
import com.alvee.fetchjson.data.remote.ApiService
import com.alvee.fetchjson.data.repository.RepositoryImpl
import com.alvee.fetchjson.domain.repository.Repository
import com.alvee.fetchjson.domain.usecase.GetPostsUsecase
import com.alvee.fetchjson.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideApplication(app: Application): Application = app

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
    fun provideApiService(retrofit: Retrofit) : ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService): Repository {
        return RepositoryImpl(apiService = apiService)
    }

    @Provides
    @Singleton
    fun provideGetPostsUseCase(repository: Repository): GetPostsUsecase{
        return GetPostsUsecase(
            repository = repository
        )
    }
}