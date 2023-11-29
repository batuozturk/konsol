package com.batuhan.realtimedatabase

import com.batuhan.core.util.retrofit.FirebaseQualifiers
import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepository
import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepositoryImpl
import com.batuhan.realtimedatabase.data.source.RealtimeDatabaseInstanceDataSource
import com.batuhan.realtimedatabase.data.source.RealtimeDatabaseInstancePagingSource
import com.batuhan.realtimedatabase.data.source.RealtimeDatabaseInstanceService
import com.batuhan.realtimedatabase.data.source.RealtimeDatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object RealtimeDatabaseModule {

    @Provides
    @ViewModelScoped
    fun provideRealtimeDatabaseInstanceService(@FirebaseQualifiers.RealtimeDatabaseManagement retrofit: Retrofit): RealtimeDatabaseInstanceService {
        return retrofit.create(RealtimeDatabaseInstanceService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideRealtimeDatabaseService(@FirebaseQualifiers.RealtimeDatabase retrofit: Retrofit): RealtimeDatabaseService {
        return retrofit.create(RealtimeDatabaseService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideRepository(
        realtimeDatabaseInstanceDataSource: RealtimeDatabaseInstanceDataSource,
        instanceService: RealtimeDatabaseInstanceService
    ): RealtimeDatabaseInstanceRepository {
        return RealtimeDatabaseInstanceRepositoryImpl(
            realtimeDatabaseInstanceDataSource,
            instanceService
        )
    }
}
