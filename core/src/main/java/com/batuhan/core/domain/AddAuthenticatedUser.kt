package com.batuhan.core.domain

import com.batuhan.core.data.OauthUser
import com.batuhan.core.data.repository.AuthRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class AddAuthenticatedUser @Inject constructor(private val repository: AuthRepository) {

    data class Params(val user: OauthUser)

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(repository.addAuthenticatedUser(params.user))
        }.getOrElse {
            Result.Error(ExceptionType.ROOM_DB_ERROR, it)
        }
    }
}
