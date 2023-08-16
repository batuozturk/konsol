package com.batuhan.firestore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.batuhan.firestore.data.model.Database
import com.batuhan.firestore.data.model.Document
import com.batuhan.firestore.data.model.DocumentMask
import com.batuhan.firestore.data.model.FirestoreLocation
import com.batuhan.firestore.data.source.remote.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val firestoreRemoteDataSource: FirestoreRemoteDataSource,
    private val firestoreService: FirestoreService
) :
    FirestoreRepository {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override suspend fun listDatabases(projectId: String) =
        firestoreRemoteDataSource.listDatabases(projectId)

    override suspend fun getFirestoreDatabaseOperation(operationName: String) =
        firestoreRemoteDataSource.getFirestoreDatabaseOperation(operationName)

    override suspend fun createDatabase(projectId: String, databaseId: String, database: Database) =
        firestoreRemoteDataSource.createDatabase(projectId, databaseId, database)

    override suspend fun createDocument(
        path: String,
        collectionId: String,
        documentId: String?,
        document: Document
    ) = firestoreRemoteDataSource.createDocument(path, collectionId, documentId, document)

    override fun listDocuments(path: String, collectionId: String): Flow<PagingData<Document>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                ListDocumentsPagingSource(firestoreService).apply {
                    setAttributes(path, collectionId)
                }
            }
        ).flow

    override fun listCollectionIds(path: String): Flow<PagingData<String>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                ListCollectionIdsPagingSource(firestoreService).apply {
                    setParent(path)
                }
            }
        ).flow

    override suspend fun getDocument(documentPath: String) =
        firestoreRemoteDataSource.getDocument(documentPath)

    override suspend fun deleteDocument(documentPath: String) =
        firestoreRemoteDataSource.deleteDocument(documentPath)

    override fun listFirestoreLocations(projectId: String): Flow<PagingData<FirestoreLocation>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                ListFirestoreLocationsPagingSource(firestoreService).apply {
                    setProjectId(projectId)
                }
            }
        ).flow

    override suspend fun patchDocument(
        documentPath: String,
        documentMask: DocumentMask,
        document: Document
    ) = firestoreRemoteDataSource.patchDocument(documentPath, documentMask, document)

    override suspend fun patchDatabase(
        databasePath: String,
        databaseUpdateMask: String,
        database: Database
    ) = firestoreRemoteDataSource.patchDatabase(databasePath, databaseUpdateMask, database)
}
