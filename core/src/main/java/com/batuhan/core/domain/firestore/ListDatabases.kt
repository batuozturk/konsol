package com.batuhan.core.domain.firestore

import com.batuhan.core.data.model.firestore.ListDatabaseResponse
import com.batuhan.core.data.repository.firestore.FirestoreRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
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
