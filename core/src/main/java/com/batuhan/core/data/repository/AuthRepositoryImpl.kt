package com.batuhan.core.data.repository

import com.batuhan.core.data.OauthUser
import com.batuhan.core.data.source.local.AuthLocalDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val localDataSource: AuthLocalDataSource) :
    AuthRepository {
    override suspend fun getAuthenticatedUser(): OauthUser? {
        return localDataSource.getAuthenticatedUser()
    }

    override suspend fun addAuthenticatedUser(authState: OauthUser) {
        return localDataSource.addAuthenticatedUser(authState)
    }

    override suspend fun deleteAuthenticatedUser(oauthUser: OauthUser) {
        return localDataSource.deleteAuthenticatedUser(oauthUser)
    }

    override suspend fun updateAuthenticatedUser(oauthUser: OauthUser) {
        return localDataSource.updateAuthenticatedUser(oauthUser)
    }
}
