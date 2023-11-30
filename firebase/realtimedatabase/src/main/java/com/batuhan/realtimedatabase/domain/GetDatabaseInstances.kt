package com.batuhan.realtimedatabase.domain

import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepository
import javax.inject.Inject

class GetDatabaseInstances @Inject constructor(private val repository: RealtimeDatabaseInstanceRepository) {

    data class Params(val projectId: String)

    operator fun invoke(params: Params) = repository.getDatabaseInstanceList(params.projectId)
}
