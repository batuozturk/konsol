package com.batuhan.testlab.domain.testing

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.testlab.data.model.devicecatalog.TestEnvironmentCatalog
import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject

class GetTestEnvironmentCatalog @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<TestEnvironmentCatalog> {
        return runCatching {
            Result.Success(testLabRepository.getTestEnvironmentCatalog(params.projectId))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
