package com.batuhan.testlab.domain.result

import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject

class GetEnvironmentList @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(val projectId: String, val historyId: String, val executionId: String)

    operator fun invoke(params: Params) =
        testLabRepository.getEnvironmentList(params.projectId, params.historyId, params.executionId)
}
