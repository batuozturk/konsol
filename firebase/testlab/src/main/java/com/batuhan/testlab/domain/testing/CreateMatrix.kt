package com.batuhan.testlab.domain.testing

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.testlab.data.model.matrix.TestMatrix
import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject

class CreateMatrix @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(val projectId: String, val testMatrix: TestMatrix)

    suspend operator fun invoke(params: Params): Result<TestMatrix> {
        return runCatching {
            Result.Success(testLabRepository.createMatrix(params.projectId, params.testMatrix))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
