package com.batuhan.core.data.source.remote

import com.batuhan.core.data.model.DefaultBucket
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FirebaseCloudStorageService {

    companion object {
        private const val PATH_VERSION = "v1alpha"
    }

    @GET("$PATH_VERSION/projects/{projectId}/defaultBucket")
    suspend fun getDefaultBucket(@Path("projectId") projectId: String): DefaultBucket

    @POST("$PATH_VERSION/projects/{projectId}/defaultBucket")
    suspend fun createDefaultBucket(
        @Path("projectId") projectId: String,
        @Body defaultBucket: DefaultBucket
    ): DefaultBucket

    @POST("$PATH_VERSION/{bucketId}:addFirebase")
    suspend fun addFirebase(
        @Path("bucketId", encoded = true) bucketId: String
    )
}
