package com.batuhan.core.domain

import com.batuhan.core.data.OauthUser
import com.batuhan.core.data.repository.AuthRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class GetAuthenticatedUser @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(): Result<OauthUser> {
        return runCatching {
            Result.Success(repository.getAuthenticatedUser())
        }.getOrElse {
            Result.Error(ExceptionType.ROOM_DB_ERROR, it)
        }
    }
}
