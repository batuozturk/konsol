package com.batuhan.management.domain.firebase

import com.batuhan.core.data.model.management.AnalyticsDetailsResponse
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class GetAnalyticsDetails @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<AnalyticsDetailsResponse> {
        return runCatching {
            Result.Success(
                managementRepository.getAnalyticsDetails(params.projectId)
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
