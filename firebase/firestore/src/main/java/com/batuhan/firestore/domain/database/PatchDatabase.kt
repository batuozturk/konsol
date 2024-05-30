package com.batuhan.firestore.domain.database

import com.batuhan.core.data.model.Operation
import com.batuhan.core.data.model.firestore.Database
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.data.repository.firestore.FirestoreRepository
import javax.inject.Inject
import com.batuhan.core.util.Result

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
