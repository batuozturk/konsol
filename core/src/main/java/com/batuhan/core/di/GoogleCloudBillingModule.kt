package com.batuhan.core.di

import com.batuhan.core.util.retrofit.GoogleQualifiers.GoogleCloudBilling
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
object GoogleCloudBillingModule {

    @Singleton
    @Provides
    @GoogleCloudBilling
    fun provideGoogleCloudBillingRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://cloudbilling.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
