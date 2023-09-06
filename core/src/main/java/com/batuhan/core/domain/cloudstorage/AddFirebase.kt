package com.batuhan.core.domain.cloudstorage

import com.batuhan.core.data.repository.CloudStorageRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class AddFirebase @Inject constructor(private val cloudStorageRepository: CloudStorageRepository) {

    data class Params(val bucketId: String)

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(cloudStorageRepository.addFirebase(params.bucketId))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
