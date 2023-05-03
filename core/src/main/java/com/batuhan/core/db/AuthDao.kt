package com.batuhan.core.db

import androidx.room.*
import com.batuhan.core.data.OauthUser

@Dao
interface AuthDao {

    @Query("SELECT * FROM oauthUser LIMIT 1")
    suspend fun getAuthenticatedUser(): OauthUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAuthenticatedUser(authState: OauthUser)

    @Delete
    suspend fun deleteAuthenticatedUser(authState: OauthUser)
}
