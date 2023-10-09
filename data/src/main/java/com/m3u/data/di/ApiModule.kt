@file:Suppress("unused")

package com.m3u.data.di

import com.m3u.core.architecture.configuration.SharedConfiguration
import com.m3u.core.util.serialization.asConverterFactory
import com.m3u.data.api.DropboxApi
import com.m3u.data.api.GithubApi
import com.m3u.data.contract.Apis
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideOkhttpClient(
        configuration: SharedConfiguration
    ): OkHttpClient {
        val connectTimeout = configuration.connectTimeout.value
        val duration = Duration.of(connectTimeout.toLong(), ChronoUnit.MILLIS)
        return OkHttpClient.Builder()
            .connectTimeout(duration)
            .callTimeout(duration)
            .build()
    }

    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        json: Json
    ): Retrofit.Builder {
        val mediaType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(mediaType))
    }

    @Provides
    fun provideGithubApi(
        builder: Retrofit.Builder
    ): GithubApi = builder
        .baseUrl(Apis.GITHUB_BASE_URL)
        .build()
        .create()

    @Provides
    fun provideDropboxApi(
        builder: Retrofit.Builder
    ): DropboxApi = builder
        .baseUrl(Apis.DROPBOX_BASE_URL)
        .build()
        .create()
}