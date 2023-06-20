package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.model.FinalizeLocationRequest
import com.batuhan.management.data.model.Operation
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class FinalizeLocation @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String, val locationId: String)

    suspend operator fun invoke(params: Params): Result<Operation> {
        return runCatching {
            Result.Success(
                managementRepository.finalizeLocation(
                    params.projectId,
                    FinalizeLocationRequest(locationId = params.locationId)
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
