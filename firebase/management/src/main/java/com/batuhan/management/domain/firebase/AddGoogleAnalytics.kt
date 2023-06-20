package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.model.AddGoogleAnalyticsRequest
import com.batuhan.management.data.model.Operation
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class AddGoogleAnalytics @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val analyticsAccountId: String, val project: String)

    suspend operator fun invoke(params: Params): Result<Operation> {
        val addGoogleAnalyticsRequest =
            AddGoogleAnalyticsRequest(analyticsAccountId = params.analyticsAccountId)
        return runCatching {
            Result.Success(
                managementRepository.addGoogleAnalytics(
                    params.project,
                    addGoogleAnalyticsRequest
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
