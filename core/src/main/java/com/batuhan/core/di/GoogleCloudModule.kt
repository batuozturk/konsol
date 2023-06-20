package com.batuhan.core.di

import com.batuhan.core.util.retrofit.GoogleQualifiers.GoogleCloudProjects
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleCloudModule {

    @Provides
    @Singleton
    @GoogleCloudProjects
    fun provideGoogleCloudRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://cloudresourcemanager.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
