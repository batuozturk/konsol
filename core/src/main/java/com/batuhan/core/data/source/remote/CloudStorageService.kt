package com.batuhan.core.data.source.remote

import com.batuhan.core.data.model.BucketObject
import com.batuhan.core.data.model.CloudStorageObjectResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

interface CloudStorageService {

    companion object {
        const val PATH = "storage/v1"
        const val PATH_UPLOAD = "upload/storage/v1"
        const val UPLOAD_TYPE_MEDIA = "media"
    }

    @GET("$PATH/b")
    suspend fun getStorageBuckets(
        @Query("project") projectId: String,
        @Query("maxResult") maxResult: Int = 10,
        @Query("pageToken") pageToken: String? = null
    )

    @GET("$PATH/b/{bucketName}/o")
    suspend fun getObjectList(
        @Path("bucketName", encoded = true) bucketName: String,
        @Query("delimiter", encoded = true) delimiter: String = "/",
        @Query("prefix", encoded = true) prefix: String? = null,
        @Query("maxResult") maxResult: Int = 10,
        @Query("pageToken") pageToken: String? = null
    ): CloudStorageObjectResponse

    @POST("$PATH_UPLOAD/b/{bucketName}/o")
    suspend fun uploadFile(
        @Header("Content-Length") contentLength: Long,
        @Header("Content-Type") contentType: String,
        @Path("bucketName", encoded = true) bucketName: String,
        @Query("name", encoded = true) name: String,
        @Query("uploadType") uploadType: String = UPLOAD_TYPE_MEDIA,
        @Body requestBody: RequestBody
    ): BucketObject
}
