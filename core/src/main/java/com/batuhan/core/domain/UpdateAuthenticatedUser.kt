package com.batuhan.core.domain

import com.batuhan.core.data.OauthUser
import com.batuhan.core.data.repository.AuthRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class UpdateAuthenticatedUser @Inject constructor(private val repository: AuthRepository) {

    data class Params(val oauthUser: OauthUser)

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(repository.updateAuthenticatedUser(params.oauthUser))
        }.getOrElse {
            Result.Error(ExceptionType.ROOM_DB_ERROR, it)
        }
    }
}
