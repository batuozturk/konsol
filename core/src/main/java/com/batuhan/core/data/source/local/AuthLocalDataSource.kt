package com.batuhan.core.data.source.local

import com.batuhan.core.data.OauthUser
import com.batuhan.core.db.AuthDao
import com.batuhan.core.db.AuthDatabase
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(private val database: AuthDatabase) {

    suspend fun getAuthenticatedUser() = database.authDao.getAuthenticatedUser()

    suspend fun addAuthenticatedUser(oauthUser: OauthUser) =
        database.authDao.addAuthenticatedUser(oauthUser)

    suspend fun deleteAuthenticatedUser(oauthUser: OauthUser) = database.authDao.deleteAuthenticatedUser(oauthUser)
}
