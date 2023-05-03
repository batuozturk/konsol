package com.batuhan.core.data.repository

import com.batuhan.core.data.OauthUser

interface AuthRepository {

    suspend fun getAuthenticatedUser(): OauthUser?

    suspend fun addAuthenticatedUser(authState: OauthUser)

    suspend fun deleteAuthenticatedUser(oauthUser: OauthUser)
}
