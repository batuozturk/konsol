package com.batuhan.firestore.domain.serviceusage

import com.batuhan.core.data.model.Operation
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.firestore.data.repository.serviceusage.ServiceUsageRepository
import javax.inject.Inject

class EnableService @Inject constructor(private val serviceUsageRepository: ServiceUsageRepository) {

    data class Params(val serviceName: String)

    suspend operator fun invoke(params: Params): Result<Operation> {
        return runCatching {
            Result.Success(serviceUsageRepository.enableService(params.serviceName))
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
