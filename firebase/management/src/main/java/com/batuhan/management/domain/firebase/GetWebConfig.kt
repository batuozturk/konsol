package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.model.WebConfig
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetWebConfig @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String, val appId: String)

    suspend operator fun invoke(params: Params): Result<WebConfig> {
        return runCatching {
            Result.Success(
                managementRepository.getWebConfig(params.projectId, params.appId)
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}