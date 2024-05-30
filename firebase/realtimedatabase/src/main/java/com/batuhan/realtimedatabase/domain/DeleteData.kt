package com.batuhan.realtimedatabase.domain

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepository
import javax.inject.Inject

class DeleteData @Inject constructor(private val realtimeDatabaseInstanceRepository: RealtimeDatabaseInstanceRepository) {

    data class Params(val url: String)

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(realtimeDatabaseInstanceRepository.deleteData(params.url))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
