package com.batuhan.core.di

import com.batuhan.core.data.repository.CloudStorageRepository
import com.batuhan.core.data.repository.CloudStorageRepositoryImpl
import com.batuhan.core.data.source.remote.CloudStorageDataSource
import com.batuhan.core.data.source.remote.CloudStorageService
import com.batuhan.core.data.source.remote.FirebaseCloudStorageService
import com.batuhan.core.util.retrofit.FirebaseQualifiers
import com.batuhan.core.util.retrofit.GoogleQualifiers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object CloudStorageModule {

    @Provides
    @ViewModelScoped
    fun provideCloudStorageService(@GoogleQualifiers.CloudStorage retrofit: Retrofit): CloudStorageService {
        return retrofit.create(CloudStorageService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideRepository(
        cloudStorageService: CloudStorageService,
        cloudStorageDataSource: CloudStorageDataSource
    ): CloudStorageRepository {
        return CloudStorageRepositoryImpl(cloudStorageService, cloudStorageDataSource)
    }

    @Provides
    @ViewModelScoped
    fun provideFirebaseCloudStorageService(@FirebaseQualifiers.FirebaseCloudStorage retrofit: Retrofit): FirebaseCloudStorageService {
        return retrofit.create(FirebaseCloudStorageService::class.java)
    }
}
