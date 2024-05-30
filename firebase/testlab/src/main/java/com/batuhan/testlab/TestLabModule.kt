package com.batuhan.testlab

import com.batuhan.core.util.retrofit.FirebaseQualifiers.TestLabTesting
import com.batuhan.core.util.retrofit.FirebaseQualifiers.TestLabToolResults
import com.batuhan.testlab.data.source.TestLabRepository
import com.batuhan.testlab.data.source.TestLabRepositoryImpl
import com.batuhan.testlab.data.source.result.TestLabToolResultsDataSource
import com.batuhan.testlab.data.source.result.TestLabToolResultsService
import com.batuhan.testlab.data.source.testing.TestLabTestingDataSource
import com.batuhan.testlab.data.source.testing.TestLabTestingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object TestLabModule {

    @Provides
    @ViewModelScoped
    fun provideTestLabTestingService(@TestLabTesting retrofit: Retrofit): TestLabTestingService {
        return retrofit.create(TestLabTestingService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideTestLabToolResultsService(@TestLabToolResults retrofit: Retrofit): TestLabToolResultsService {
        return retrofit.create(TestLabToolResultsService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideTestLabRepository(
        testLabTestingDataSource: TestLabTestingDataSource,
        testLabToolResultsDataSource: TestLabToolResultsDataSource
    ): TestLabRepository {
        return TestLabRepositoryImpl(testLabTestingDataSource, testLabToolResultsDataSource)
    }
}
