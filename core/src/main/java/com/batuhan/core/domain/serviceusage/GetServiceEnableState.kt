package com.batuhan.core.domain.serviceusage

import com.batuhan.core.data.model.ApiService
import com.batuhan.core.data.repository.serviceusage.ServiceUsageRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class GetServiceEnableState @Inject constructor(private val serviceUsageRepository: ServiceUsageRepository) {

    data class Params(val serviceName: String)

    suspend operator fun invoke(params: Params): Result<ApiService> {
        return runCatching {
            Result.Success(serviceUsageRepository.getServiceEnableState(params.serviceName))
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}