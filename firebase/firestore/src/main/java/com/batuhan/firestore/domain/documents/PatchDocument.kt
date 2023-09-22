package com.batuhan.firestore.domain.documents

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.firestore.Document
import com.batuhan.core.data.model.firestore.DocumentMask
import com.batuhan.core.data.repository.firestore.FirestoreRepository
import javax.inject.Inject

class PatchDocument @Inject constructor(private val firestoreRepository: FirestoreRepository) {

    data class Params(val documentMask: DocumentMask, val document: Document)

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(
                firestoreRepository.patchDocument(
                    params.document.name!!,
                    params.documentMask,
                    params.document
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
