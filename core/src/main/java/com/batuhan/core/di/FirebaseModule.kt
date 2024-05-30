package com.batuhan.core.di

import com.batuhan.core.util.retrofit.FirebaseQualifiers.*
import com.batuhan.core.util.retrofit.KonsolAuthenticator
import com.batuhan.core.util.retrofit.KonsolInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideClient(
        interceptor: KonsolInterceptor,
        authenticator: KonsolAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator).readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Management
    fun provideFirebaseManagementRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://firebase.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    @Firestore
    fun provideFirestoreRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://firestore.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    @CloudMessaging
    fun provideCloudMessagingRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://fcm.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    @TestLabTesting
    fun provideTestLabTestingRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://testing.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    @TestLabToolResults
    fun provideTestLabToolResultsRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://toolresults.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    @FirebaseCloudStorage
    fun provideFirebaseCloudStorageRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://firebasestorage.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    @RealtimeDatabase
    fun provideRealtimeDatabaseRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder().baseUrl("http://localhost/").client(client).addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    @Provides
    @Singleton
    @RealtimeDatabaseManagement
    fun provideRealtimeDatabaseManagementRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://firebasedatabase.googleapis.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
