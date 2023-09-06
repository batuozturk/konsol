package com.batuhan.cloudstorage.domain

import com.batuhan.core.data.model.DefaultBucket
import com.batuhan.core.data.repository.CloudStorageRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class CreateDefaultBucket @Inject constructor(private val cloudStorageRepository: CloudStorageRepository) {

    data class Params(val projectId: String, val defaultBucket: DefaultBucket)

    suspend operator fun invoke(params: Params): Result<DefaultBucket> {
        return runCatching {
            Result.Success(
                cloudStorageRepository.createDefaultBucket(
                    params.projectId,
                    params.defaultBucket
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
