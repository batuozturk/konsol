package com.batuhan.testlab.domain.result

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.testlab.data.model.execution.ExecutionEnvironment
import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject

class GetEnvironment @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(
        val projectId: String,
        val historyId: String,
        val executionId: String,
        val environmentId: String
    )

    suspend operator fun invoke(params: Params): Result<ExecutionEnvironment> {
        return runCatching {
            Result.Success(
                testLabRepository.getEnvironment(
                    params.projectId,
                    params.historyId,
                    params.executionId,
                    params.environmentId
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
