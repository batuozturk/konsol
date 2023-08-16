package com.batuhan.core.di

import com.batuhan.core.util.retrofit.FConsoleAuthenticator
import com.batuhan.core.util.retrofit.FConsoleInterceptor
import com.batuhan.core.util.retrofit.FirebaseQualifiers.*
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
object FirebaseModule {

    @Provides
    @Singleton
    fun provideClient(
        interceptor: FConsoleInterceptor,
        authenticator: FConsoleAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)
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
}
