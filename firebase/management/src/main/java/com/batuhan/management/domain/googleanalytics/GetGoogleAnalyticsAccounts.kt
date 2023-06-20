package com.batuhan.management.domain.googleanalytics

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.model.AnalyticsAccountResponse
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetGoogleAnalyticsAccounts @Inject constructor(private val managementRepository: ManagementRepository) {

    suspend operator fun invoke(): Result<AnalyticsAccountResponse> {
        return runCatching {
            Result.Success(managementRepository.getGoogleAnalyticsAccounts())
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
