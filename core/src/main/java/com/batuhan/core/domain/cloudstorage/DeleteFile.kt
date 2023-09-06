package com.batuhan.core.domain.cloudstorage

import com.batuhan.core.data.repository.CloudStorageRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import retrofit2.Response
import javax.inject.Inject

class DeleteFile @Inject constructor(
    private val cloudStorageRepository: CloudStorageRepository
) {
    data class Params(
        val bucketName: String,
        val objectName: String
    )

    suspend operator fun invoke(params: Params): Result<Response<Unit>> {
        return runCatching {
            Result.Success(
                cloudStorageRepository.deleteFile(
                    params.bucketName,
                    params.objectName
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
