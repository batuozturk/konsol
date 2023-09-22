package com.batuhan.firestore

import com.batuhan.core.util.retrofit.FirebaseQualifiers.Firestore
import com.batuhan.core.data.repository.firestore.FirestoreRepository
import com.batuhan.core.data.repository.firestore.FirestoreRepositoryImpl
import com.batuhan.core.data.source.remote.firestore.FirestoreRemoteDataSource
import com.batuhan.core.data.source.remote.firestore.FirestoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object FirestoreModule {

    @Provides
    @ViewModelScoped
    fun provideService(@Firestore retrofit: Retrofit): FirestoreService {
        return retrofit.create(FirestoreService::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideRepository(
        firestoreRemoteDataSource: FirestoreRemoteDataSource,
        firestoreService: FirestoreService
    ): FirestoreRepository {
        return FirestoreRepositoryImpl(firestoreRemoteDataSource, firestoreService)
    }
}
