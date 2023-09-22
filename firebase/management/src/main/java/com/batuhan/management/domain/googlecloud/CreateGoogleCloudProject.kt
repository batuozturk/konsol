package com.batuhan.management.domain.googlecloud

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.CreateProjectRequest
import com.batuhan.core.data.model.management.Operation
import com.batuhan.core.data.repository.management.ManagementRepository
import javax.inject.Inject

class CreateGoogleCloudProject @Inject constructor(private val repository: ManagementRepository) {

    data class Params(val createProjectRequest: CreateProjectRequest)

    suspend operator fun invoke(params: Params): Result<Operation> {
        return runCatching {
            val response = repository.createGoogleCloudProject(params.createProjectRequest)
            Result.Success(response)
        }.getOrElse {
            Result.Error(ExceptionType.GOOGLE_CLOUD_API_EXCEPTION, it)
        }
    }
}
