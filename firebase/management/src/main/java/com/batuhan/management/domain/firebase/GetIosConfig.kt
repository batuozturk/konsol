package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.IosConfig
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetIosConfig @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String, val appId: String)

    suspend operator fun invoke(params: Params): Result<IosConfig> {
        return runCatching {
            Result.Success(
                managementRepository.getIosConfig(params.projectId, params.appId)
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
