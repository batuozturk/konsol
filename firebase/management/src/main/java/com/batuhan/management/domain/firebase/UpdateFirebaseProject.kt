package com.batuhan.management.domain.firebase

import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.UpdateFirebaseProjectRequest
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class UpdateFirebaseProject @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(
        val projectId: String,
        val updatedParams: String,
        val updateFirebaseProjectRequest: UpdateFirebaseProjectRequest
    )

    suspend operator fun invoke(params: Params): Result<FirebaseProject> {
        return runCatching {
            Result.Success(
                managementRepository.updateFirebaseProject(
                    params.projectId,
                    params.updatedParams,
                    params.updateFirebaseProjectRequest
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
