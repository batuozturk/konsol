package com.batuhan.core.di

import com.batuhan.core.data.repository.AuthRepository
import com.batuhan.core.data.repository.AuthRepositoryImpl
import com.batuhan.core.data.source.local.AuthLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(localDataSource: AuthLocalDataSource): AuthRepository {
        return AuthRepositoryImpl(localDataSource)
    }
}
