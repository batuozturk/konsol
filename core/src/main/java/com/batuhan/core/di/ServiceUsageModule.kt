package com.batuhan.core.di

import com.batuhan.core.data.repository.serviceusage.ServiceUsageRepository
import com.batuhan.core.data.repository.serviceusage.ServiceUsageRepositoryImpl
import com.batuhan.core.data.source.remote.serviceusage.ServiceUsageRemoteDataSource
import com.batuhan.core.data.source.remote.serviceusage.ServiceUsageService
import com.batuhan.core.util.retrofit.GoogleQualifiers.GoogleCloudServiceUsage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object ServiceUsageModule {

    @Provides
    @ViewModelScoped
    fun provideServiceUsageService(@GoogleCloudServiceUsage retrofit: Retrofit): ServiceUsageService {
        return retrofit.create(ServiceUsageService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideServiceUsageRepository(
        serviceUsageRemoteDataSource: ServiceUsageRemoteDataSource
    ): ServiceUsageRepository {
        return ServiceUsageRepositoryImpl(serviceUsageRemoteDataSource)
    }
}