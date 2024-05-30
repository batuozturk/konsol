package com.batuhan.realtimedatabase.domain

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepository
import javax.inject.Inject

class CreateDatabaseInstance @Inject constructor(private val repository: RealtimeDatabaseInstanceRepository) {

    data class Params(
        val projectId: String,
        val locationId: String,
        val databaseId: String,
        val databaseInstance: DatabaseInstance
    )

    suspend operator fun invoke(params: Params): Result<DatabaseInstance> {
        return runCatching {
            Result.Success(
                repository.createDatabaseInstance(
                    params.projectId,
                    params.locationId,
                    params.databaseId,
                    params.databaseInstance
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
