package com.batuhan.firestore.domain.database

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.firestore.data.model.Database
import com.batuhan.firestore.data.model.ListDatabaseResponse
import com.batuhan.firestore.data.repository.FirestoreRepository
import javax.inject.Inject

class ListDatabases @Inject constructor(private val firestoreRepository: FirestoreRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<ListDatabaseResponse> {
        return runCatching {
            Result.Success(
                firestoreRepository.listDatabases(
                    params.projectId
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
