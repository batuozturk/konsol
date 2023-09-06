package com.batuhan.core.domain.cloudstorage

import com.batuhan.core.data.model.DefaultBucket
import com.batuhan.core.data.repository.CloudStorageRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class GetDefaultBucket @Inject constructor(private val cloudStorageRepository: CloudStorageRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<DefaultBucket> {
        return runCatching {
            Result.Success(cloudStorageRepository.getDefaultBucket(params.projectId))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
