package com.batuhan.management.domain.googlecloud

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class DeleteGoogleCloudProject @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(
                managementRepository.deleteGoogleCloudProject(params.projectId)
            )
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
