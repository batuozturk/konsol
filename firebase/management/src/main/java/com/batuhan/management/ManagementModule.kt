package com.batuhan.management

import com.batuhan.core.data.repository.management.ManagementRepository
import com.batuhan.core.data.repository.management.ManagementRepositoryImpl
import com.batuhan.core.data.source.remote.management.ManagementRemoteDataSource
import com.batuhan.core.data.source.remote.management.ManagementService
import com.batuhan.core.data.source.remote.management.googleanalytics.GoogleAnalyticsRemoteDataSource
import com.batuhan.core.data.source.remote.management.googleanalytics.GoogleAnalyticsService
import com.batuhan.core.data.source.remote.management.googlecloud.GoogleCloudRemoteDataSource
import com.batuhan.core.data.source.remote.management.googlecloud.GoogleCloudService
import com.batuhan.core.data.source.remote.management.googlecloud.billing.GoogleCloudBillingDataSource
import com.batuhan.core.data.source.remote.management.googlecloud.billing.GoogleCloudBillingService
import com.batuhan.core.util.retrofit.FirebaseQualifiers.Management
import com.batuhan.core.util.retrofit.GoogleQualifiers.*
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
    fun provideManagementService(@Management retrofit: Retrofit): ManagementService {
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
    fun provideGoogleCloudBillingService(@GoogleCloudBilling retrofit: Retrofit): GoogleCloudBillingService {
        return retrofit.create(GoogleCloudBillingService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideRepository(
        remoteDataSource: ManagementRemoteDataSource,
        googleCloudRemoteDataSource: GoogleCloudRemoteDataSource,
        googleAnalyticsRemoteDataSource: GoogleAnalyticsRemoteDataSource,
        googleAnalyticsService: GoogleAnalyticsService,
        managementService: ManagementService,
        googleCloudBillingDataSource: GoogleCloudBillingDataSource,
        getGoogleCloudBillingService: GoogleCloudBillingService
    ): ManagementRepository {
        return ManagementRepositoryImpl(
            remoteDataSource,
            managementService,
            googleCloudRemoteDataSource,
            googleAnalyticsRemoteDataSource,
            googleAnalyticsService,
            googleCloudBillingDataSource,
            getGoogleCloudBillingService
        )
    }
}
