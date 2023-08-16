package com.batuhan.firestore.domain.database

import com.batuhan.core.data.model.Operation
import com.batuhan.core.util.ExceptionType
import com.batuhan.firestore.data.model.Document
import com.batuhan.firestore.data.repository.FirestoreRepository
import javax.inject.Inject
import com.batuhan.core.util.Result
import com.batuhan.firestore.data.model.Database

class PatchDatabase @Inject constructor(private val firestoreRepository: FirestoreRepository) {

    data class Params(val updateMask: String, val database: Database)

    suspend operator fun invoke(params: Params): Result<Operation>{
        return runCatching {
            Result.Success(firestoreRepository.patchDatabase(params.database.name!! ,params.updateMask, params.database))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
