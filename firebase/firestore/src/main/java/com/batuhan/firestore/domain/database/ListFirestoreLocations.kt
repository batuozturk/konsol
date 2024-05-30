package com.batuhan.firestore.domain.database

import com.batuhan.core.data.repository.firestore.FirestoreRepository
import javax.inject.Inject

class ListFirestoreLocations @Inject constructor(private val firestoreRepository: FirestoreRepository){

    data class Params(val projectId: String)

    operator fun invoke(params: Params) = firestoreRepository.listFirestoreLocations(params.projectId)
}