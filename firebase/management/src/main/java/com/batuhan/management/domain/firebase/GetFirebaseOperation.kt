package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.Operation

class GetFirebaseOperation @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val operationId: String)

    suspend operator fun invoke(params: Params): Result<Operation>{
        return runCatching {
            Result.Success(managementRepository.getFirebaseOperation(params.operationId))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}