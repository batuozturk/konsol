package com.batuhan.firestore.data.source.remote

import com.batuhan.firestore.data.model.Database
import com.batuhan.firestore.data.model.Document
import com.batuhan.firestore.data.model.DocumentMask
import javax.inject.Inject

class FirestoreRemoteDataSource @Inject constructor(private val firestoreService: FirestoreService) {

    suspend fun listDatabases(projectId: String) = firestoreService.listDatabases(projectId)

    suspend fun getFirestoreDatabaseOperation(operationName: String) =
        firestoreService.getFirestoreDatabaseOperation(operationName)

    suspend fun createDatabase(projectId: String, databaseId: String, database: Database) =
        firestoreService.createDatabase(projectId, databaseId, database)

    suspend fun createDocument(
        parent: String,
        collectionId: String,
        documentId: String?,
        document: Document
    ) = firestoreService.createDocument(parent, collectionId, documentId, document)

    suspend fun getDocument(name: String) = firestoreService.getDocument(name)

    suspend fun deleteDocument(name: String) = firestoreService.deleteDocument(name)

    suspend fun patchDocument(
        documentPath: String,
        documentMask: DocumentMask,
        document: Document
    ) = firestoreService.patchDocument(
        documentPath,
        *documentMask.fieldPaths!!.map { it }.toTypedArray(),
        document = document
    )

    suspend fun patchDatabase(
        databasePath: String,
        databaseUpdateMask: String,
        database: Database
    ) = firestoreService.patchDatabase(databasePath,databaseUpdateMask,database)
}
