package com.batuhan.core.data.repository.firestore

import androidx.paging.PagingData
import com.batuhan.core.data.model.Operation
import com.batuhan.core.data.model.firestore.*
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {

    suspend fun listDatabases(projectId: String): ListDatabaseResponse

    suspend fun getFirestoreDatabaseOperation(operationName: String): Operation

    suspend fun createDatabase(projectId: String, databaseId: String, database: Database): Operation

    suspend fun createDocument(
        path: String,
        collectionId: String,
        documentId: String?,
        document: Document
    ): Document

    fun listDocuments(path: String, collectionId: String): Flow<PagingData<Document>>

    fun listCollectionIds(path: String): Flow<PagingData<String>>

    suspend fun getDocument(documentPath: String): Document

    suspend fun deleteDocument(documentPath: String)

    fun listFirestoreLocations(projectId: String): Flow<PagingData<FirestoreLocation>>

    suspend fun patchDocument(documentPath: String, documentMask: DocumentMask, document: Document)

    suspend fun patchDatabase(
        databasePath: String,
        databaseUpdateMask: String,
        database: Database
    ): Operation
}
