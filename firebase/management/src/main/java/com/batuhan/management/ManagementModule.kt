package com.batuhan.management

import com.batuhan.core.util.retrofit.FirebaseQualifiers
import com.batuhan.core.util.retrofit.GoogleQualifiers.*
import com.batuhan.management.data.repository.ManagementRepository
import com.batuhan.management.data.repository.ManagementRepositoryImpl
import com.batuhan.management.data.source.remote.*
import com.batuhan.management.data.source.remote.firebase.ManagementRemoteDataSource
import com.batuhan.management.data.source.remote.firebase.ManagementService
import com.batuhan.management.data.source.remote.googleanalytics.GoogleAnalyticsRemoteDataSource
import com.batuhan.management.data.source.remote.googleanalytics.GoogleAnalyticsService
import com.batuhan.management.data.source.remote.googlecloud.GoogleCloudRemoteDataSource
import com.batuhan.management.data.source.remote.googlecloud.GoogleCloudService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object ManagementModule {

    @Provides
    @ViewModelScoped
    fun provideManagementService(@FirebaseQualifiers.Management retrofit: Retrofit): ManagementService {
        return retrofit.create(ManagementService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideGoogleCloudService(@GoogleCloudProjects retrofit: Retrofit): GoogleCloudService {
        return retrofit.create(GoogleCloudService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideGoogleAnalyticsService(@GoogleAnalytics retrofit: Retrofit): GoogleAnalyticsService {
        return retrofit.create(GoogleAnalyticsService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideRepository(
        remoteDataSource: ManagementRemoteDataSource,
        googleCloudRemoteDataSource: GoogleCloudRemoteDataSource,
        googleAnalyticsRemoteDataSource: GoogleAnalyticsRemoteDataSource,
        managementService: ManagementService
    ): ManagementRepository {
        return ManagementRepositoryImpl(
            remoteDataSource,
            managementService,
            googleCloudRemoteDataSource,
            googleAnalyticsRemoteDataSource
        )
    }
}