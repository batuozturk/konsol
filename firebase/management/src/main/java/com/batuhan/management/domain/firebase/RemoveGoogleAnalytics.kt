package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.RemoveGoogleAnalyticsRequest
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class RemoveGoogleAnalytics @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(
        val projectId: String,
        val removeGoogleAnalyticsRequest: RemoveGoogleAnalyticsRequest
    )

    suspend operator fun invoke(params: Params): Result<Unit> {
        return runCatching {
            Result.Success(
                managementRepository.removeGoogleAnalytics(
                    params.projectId,
                    params.removeGoogleAnalyticsRequest
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
