package com.batuhan.realtimedatabase.domain

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepository
import com.google.gson.JsonElement
import javax.inject.Inject

class GetDatabase @Inject constructor(private val repository: RealtimeDatabaseInstanceRepository) {

    data class Params(val url: String)

    suspend operator fun invoke(params: Params): Result<Map<String, JsonElement?>?> {
        return runCatching {
            Result.Success(repository.getDatabase(params.url)?.asMap())
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
