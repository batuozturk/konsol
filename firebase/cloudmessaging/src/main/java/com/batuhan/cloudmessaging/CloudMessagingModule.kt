package com.batuhan.cloudmessaging

import com.batuhan.cloudmessaging.data.repository.CloudMessagingRepository
import com.batuhan.cloudmessaging.data.repository.CloudMessagingRepositoryImpl
import com.batuhan.cloudmessaging.data.source.CloudMessagingDataSource
import com.batuhan.cloudmessaging.data.source.CloudMessagingService
import com.batuhan.core.util.retrofit.FirebaseQualifiers.CloudMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object CloudMessagingModule {

    @Provides
    @ViewModelScoped
    fun provideCloudMessagingService(@CloudMessaging retrofit: Retrofit): CloudMessagingService {
        return retrofit.create(CloudMessagingService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideRepository(cloudMessagingDataSource: CloudMessagingDataSource): CloudMessagingRepository {
        return CloudMessagingRepositoryImpl(cloudMessagingDataSource)
    }
}
