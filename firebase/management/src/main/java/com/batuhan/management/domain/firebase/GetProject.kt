package com.batuhan.management.domain.firebase

import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.management.data.repository.ManagementRepository
import javax.inject.Inject

class GetProject @Inject constructor(private val managementRepository: ManagementRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<FirebaseProject> {
        return runCatching {
            Result.Success(
                managementRepository.getProject(params.projectId)
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}