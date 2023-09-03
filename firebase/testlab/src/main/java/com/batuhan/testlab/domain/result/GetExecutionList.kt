package com.batuhan.testlab.domain.result

import com.batuhan.testlab.data.source.TestLabRepository
import javax.inject.Inject

class GetExecutionList @Inject constructor(private val testLabRepository: TestLabRepository) {

    data class Params(val projectId: String, val historyId: String)

    operator fun invoke(params: Params) =
        testLabRepository.getExecutionList(params.projectId, params.historyId)
}
