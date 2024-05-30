package com.batuhan.firestore.domain.documents

import com.batuhan.core.data.repository.firestore.FirestoreRepository
import javax.inject.Inject

class ListCollectionIds @Inject constructor(private val firestoreRepository: FirestoreRepository) {

    data class Params(val path: String)

    operator fun invoke(params: Params) = firestoreRepository.listCollectionIds(params.path)
}
