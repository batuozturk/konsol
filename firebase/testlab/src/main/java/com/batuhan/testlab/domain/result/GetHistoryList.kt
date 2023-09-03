package com.batuhan.testlab.domain.result

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.testlab.data.model.execution.HistoryListResponse
import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject

class GetHistoryList @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(val projectId: String)

    suspend operator fun invoke(params: Params): Result<HistoryListResponse> {
        return runCatching {
            Result.Success(testLabRepository.getHistoryList(params.projectId))
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
