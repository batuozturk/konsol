package com.batuhan.core.domain.cloudstorage

import com.batuhan.core.data.model.BucketObject
import com.batuhan.core.data.repository.CloudStorageRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class UploadFile @Inject constructor(
    private val cloudStorageRepository: CloudStorageRepository
) {
    data class Params(
        val contentLength: Long?,
        val contentType: String?,
        val bucketName: String,
        val name: String,
        val data: ByteArray?,
    )

    suspend operator fun invoke(params: Params): Result<BucketObject> {
        return runCatching {
            Result.Success(
                cloudStorageRepository.uploadFile(
                    params.contentLength,
                    params.contentType,
                    params.bucketName,
                    params.name,
                    params.data
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
