package com.batuhan.firestore.domain.documents

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.firestore.Document
import com.batuhan.core.data.repository.firestore.FirestoreRepository
import javax.inject.Inject

class GetDocument @Inject constructor(private val firestoreRepository: FirestoreRepository) {

    data class Params(val documentPath: String)

    suspend operator fun invoke(params: Params): Result<Document> {
        return runCatching {
            Result.Success(firestoreRepository.getDocument(params.documentPath))
        }.getOrElse { Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it) }
    }
}
