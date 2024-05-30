package com.batuhan.management.domain.googlecloud

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.ProjectBillingInfo
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetBillingInfo @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<ProjectBillingInfo> {
        return runCatching {
            Result.Success(
                managementRepository.getBillingInfo(params.projectId)
            )
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
