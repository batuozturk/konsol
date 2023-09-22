package com.batuhan.management.domain.firebase

import com.batuhan.core.data.model.management.AndroidApp
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.Operation
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class CreateAndroidApp @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String, val androidApp: AndroidApp)

    suspend operator fun invoke(params: Params): Result<Operation> {
        return runCatching {
            Result.Success(
                managementRepository.createAndroidApp(
                    params.projectId,
                    params.androidApp
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
