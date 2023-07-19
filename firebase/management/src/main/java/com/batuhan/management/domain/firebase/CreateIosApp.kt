package com.batuhan.management.domain.firebase

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.model.IosApp
import com.batuhan.management.data.model.Operation
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class CreateIosApp @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String, val iosApp: IosApp)

    suspend operator fun invoke(params: Params): Result<Operation> {
        return runCatching {
            Result.Success(
                managementRepository.createIosApp(params.projectId, params.iosApp)
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
