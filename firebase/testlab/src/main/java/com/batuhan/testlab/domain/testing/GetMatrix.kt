package com.batuhan.testlab.domain.testing

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.testlab.data.model.matrix.TestMatrix
import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject

class GetMatrix @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(val projectId: String, val testMatrixId: String)

    suspend operator fun invoke(params: Params): Result<TestMatrix> {
        return runCatching {
            Result.Success(testLabRepository.getMatrix(params.projectId, params.testMatrixId))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
