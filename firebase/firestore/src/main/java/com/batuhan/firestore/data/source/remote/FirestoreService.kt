package com.batuhan.firestore.data.source.remote

import com.batuhan.core.data.model.Operation
import com.batuhan.firestore.data.model.*
import retrofit2.http.*

interface FirestoreService {

    companion object {
        private const val PATH_VERSION = "v1"
        private const val PAGE_SIZE = 20
    }

    @GET("$PATH_VERSION/projects/{projectId}/databases")
    suspend fun listDatabases(@Path("projectId") projectId: String): ListDatabaseResponse

    @POST("$PATH_VERSION/projects/{projectId}/databases")
    suspend fun createDatabase(
        @Path("projectId") projectId: String,
        @Query("databaseId") databaseId: String,
        @Body database: Database
    ): Operation

    @GET("$PATH_VERSION/{operationName}")
    suspend fun getFirestoreDatabaseOperation(
        @Path("operationName", encoded = true) operationName: String
    ): Operation

    @POST("$PATH_VERSION/{parent}/{collectionId}")
    suspend fun createDocument(
        @Path("parent", encoded = true) parent: String,
        @Path("collectionId") collectionId: String,
        @Query("documentId") documentId: String?,
        @Body document: Document
    ): Document

    @GET("$PATH_VERSION/{parent}/{collectionId}")
    suspend fun listDocuments(
        @Path("parent", encoded = true) parent: String,
        @Path("collectionId") collectionId: String,
        @Query("pageSize") pageSize: Int = PAGE_SIZE,
        @Query("pageToken") pageToken: String? = null
    ): ListDocumentsResponse

    @POST("$PATH_VERSION/{parent}:listCollectionIds")
    suspend fun listCollectionIds(
        @Path("parent", encoded = true) parent: String,
        @Query("pageSize") pageSize: Int = PAGE_SIZE,
        @Query("pageToken") pageToken: String? = null
    ): ListCollectionIdsResponse

    @GET("$PATH_VERSION/{name}")
    suspend fun getDocument(@Path("name", encoded = true) name: String): Document

    @DELETE("$PATH_VERSION/{name}")
    suspend fun deleteDocument(@Path("name", encoded = true) name: String)

    @GET("$PATH_VERSION/projects/{projectId}/locations")
    suspend fun listFirestoreLocations(
        @Path("projectId") projectId: String,
        @Query("pageSize") pageSize: Int = PAGE_SIZE,
        @Query("pageToken") pageToken: String? = null
    ): ListFirestoreLocationsResponse

    @PATCH("$PATH_VERSION/{documentName}")
    suspend fun patchDocument(
        @Path("documentName", encoded = true) documentPath: String,
        @Query("updateMask.fieldPaths", encoded = true) vararg fieldMask: String,
        @Body document: Document
    )

    @PATCH("$PATH_VERSION/{databasePath}")
    suspend fun patchDatabase(
        @Path("databasePath", encoded = true) databasePath: String,
        @Query("updateMask", encoded = true) databaseUpdateMask: String,
        @Body database: Database
    ): Operation
}
