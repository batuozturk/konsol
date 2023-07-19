package com.batuhan.management.domain.googlecloud

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.model.ProjectBillingInfo
import com.batuhan.management.data.model.UpdateBillingInfoRequest
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class UpdateBillingInfo @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String, val updateBillingInfoRequest: UpdateBillingInfoRequest)

    suspend operator fun invoke(params: Params): Result<ProjectBillingInfo> {
        return runCatching {
            Result.Success(
                managementRepository.updateBillingInfo(
                    params.projectId,
                    params.updateBillingInfoRequest
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
