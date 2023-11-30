package com.batuhan.realtimedatabase.domain

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepository
import com.google.gson.JsonObject
import javax.inject.Inject

class PatchData @Inject constructor(private val realtimeDatabaseInstanceRepository: RealtimeDatabaseInstanceRepository) {

    data class Params(val url: String, val patchBody: JsonObject)

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(realtimeDatabaseInstanceRepository.patchData(params.url, params.patchBody))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
