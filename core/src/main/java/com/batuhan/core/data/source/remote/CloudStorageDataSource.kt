package com.batuhan.core.data.source.remote

import com.batuhan.core.data.model.DefaultBucket
import com.batuhan.core.data.source.remote.CloudStorageService.Companion.UPLOAD_TYPE_MEDIA
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CloudStorageDataSource @Inject constructor(
    private val cloudStorageService: CloudStorageService,
    private val firebaseCloudStorageService: FirebaseCloudStorageService
) {

    suspend fun uploadFile(
        contentLength: Long?,
        contentType: String?,
        bucketName: String,
        name: String,
        data: ByteArray?
    ) = cloudStorageService.uploadFile(
        contentLength = contentLength,
        contentType = contentType,
        bucketName = bucketName,
        name = name,
        requestBody =
        if (contentType != null && data != null) RequestBody.create(
            MediaType.get(contentType),
            data
        )
        else RequestBody.create(null, "".toByteArray()),
        uploadType = if (contentType != null && data != null) UPLOAD_TYPE_MEDIA else null
    )

    suspend fun getDefaultBucket(projectId: String) =
        firebaseCloudStorageService.getDefaultBucket(projectId)

    suspend fun createDefaultBucket(projectId: String, defaultBucket: DefaultBucket) =
        firebaseCloudStorageService.createDefaultBucket(projectId, defaultBucket)

    suspend fun addFirebase(bucketId: String) =
        firebaseCloudStorageService.addFirebase(bucketId)

    suspend fun deleteFile(bucketName: String, objectName: String) =
        cloudStorageService.deleteFile(bucketName, objectName)
}
