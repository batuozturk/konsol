package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.model.AndroidConfig
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetAndroidConfig @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String, val appId: String)

    suspend operator fun invoke(params: Params): Result<AndroidConfig> {
        return runCatching {
            Result.Success(
                managementRepository.getAndroidConfig(params.projectId, params.appId)
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}