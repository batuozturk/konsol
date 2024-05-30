package com.batuhan.testlab.domain.testing

import com.batuhan.core.util.ExceptionType
import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject
import com.batuhan.core.util.Result
import com.batuhan.testlab.data.model.matrix.TestState

class CancelMatrix @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(val projectId: String, val testMatrixId: String)

    suspend operator fun invoke(params: Params): Result<TestState> {
        return runCatching {
            Result.Success(testLabRepository.cancelMatrix(params.projectId, params.testMatrixId))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}