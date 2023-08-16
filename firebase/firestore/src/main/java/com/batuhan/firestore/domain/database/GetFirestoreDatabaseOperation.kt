package com.batuhan.firestore.domain.database

import com.batuhan.core.data.model.Operation
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.firestore.data.repository.FirestoreRepository
import javax.inject.Inject

class GetFirestoreDatabaseOperation @Inject constructor(private val firestoreRepository: FirestoreRepository) {

    data class Params(val operationName: String)

    suspend operator fun invoke(params: Params): Result<Operation> {
        return runCatching {
            Result.Success(
                firestoreRepository.getFirestoreDatabaseOperation(
                    params.operationName
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
