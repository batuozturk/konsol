package com.batuhan.firestore.domain.documents

import com.batuhan.core.data.repository.firestore.FirestoreRepository
import javax.inject.Inject

class ListDocuments @Inject constructor(private val firestoreRepository: FirestoreRepository) {

    data class Params(
        val path: String,
        val collectionId: String
    )

    operator fun invoke(params: Params) =
        firestoreRepository.listDocuments(params.path, params.collectionId)
}
