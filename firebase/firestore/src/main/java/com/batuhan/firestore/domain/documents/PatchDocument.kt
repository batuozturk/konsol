package com.batuhan.firestore.domain.documents

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.firestore.data.model.Document
import com.batuhan.firestore.data.model.DocumentMask
import com.batuhan.firestore.data.repository.FirestoreRepository
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
