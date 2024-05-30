package com.batuhan.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.batuhan.core.util.retrofit.GoogleQualifiers.GoogleAnalytics

@Module
@InstallIn(SingletonComponent::class)
object GoogleAnalyticsModule {

    @Provides
    @Singleton
    @GoogleAnalytics
    fun provideGoogleAnalyticsRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://www.googleapis.com/analytics/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
