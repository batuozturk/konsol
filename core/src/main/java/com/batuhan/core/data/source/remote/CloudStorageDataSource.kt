package com.batuhan.core.data.source.remote

import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CloudStorageDataSource @Inject constructor(
    private val cloudStorageService: CloudStorageService,
    private val firebaseCloudStorageService: FirebaseCloudStorageService
) {

    suspend fun uploadFile(
        contentLength: Long,
        contentType: String,
        bucketName: String,
        name: String,
        data: ByteArray
    ) = cloudStorageService.uploadFile(
        contentLength = contentLength,
        contentType = contentType,
        bucketName = bucketName,
        name = name,
        requestBody = RequestBody.create(MediaType.get(contentType), data)
    )

    suspend fun getDefaultBucket(projectId: String) =
        firebaseCloudStorageService.getDefaultBucket(projectId)
}
