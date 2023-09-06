package com.batuhan.core.data.repository

import androidx.paging.PagingData
import com.batuhan.core.data.model.BucketObject
import com.batuhan.core.data.model.DefaultBucket
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface CloudStorageRepository {

    fun getObjectList(bucketName: String, prefix: String?): Flow<PagingData<String>>

    suspend fun uploadFile(
        contentLength: Long?,
        contentType: String?,
        bucketName: String,
        name: String,
        data: ByteArray?
    ): BucketObject

    suspend fun getDefaultBucket(projectId: String): DefaultBucket

    suspend fun createDefaultBucket(projectId: String, defaultBucket: DefaultBucket): DefaultBucket

    suspend fun addFirebase(bucketId: String)

    suspend fun deleteFile(bucketName: String, objectName: String): Response<Unit>
}
