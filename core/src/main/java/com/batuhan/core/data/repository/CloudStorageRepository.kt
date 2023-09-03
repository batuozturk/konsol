package com.batuhan.core.data.repository

import androidx.paging.PagingData
import com.batuhan.core.data.model.BucketObject
import com.batuhan.core.data.model.DefaultBucket
import kotlinx.coroutines.flow.Flow

interface CloudStorageRepository {

    fun getObjectList(bucketName: String, prefix: String?): Flow<PagingData<String>>

    suspend fun uploadFile(
        contentLength: Long,
        contentType: String,
        bucketName: String,
        name: String,
        data: ByteArray
    ): BucketObject

    suspend fun getDefaultBucket(projectId: String): DefaultBucket
}
