package com.batuhan.realtimedatabase.data.source

import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import com.google.gson.JsonObject
import javax.inject.Inject

class RealtimeDatabaseInstanceDataSource @Inject constructor(
    private val service: RealtimeDatabaseInstanceService,
    private val databaseService: RealtimeDatabaseService
) {

    suspend fun createDatabaseInstance(
        projectId: String,
        locationId: String,
        databaseId: String,
        databaseInstance: DatabaseInstance
    ): DatabaseInstance {
        return service.createDatabaseInstance(projectId, locationId, databaseId, databaseInstance)
    }

    suspend fun getDatabase(
        url: String
    ): JsonObject? = databaseService.getDatabase(url)

    suspend fun deleteData(url: String) = databaseService.deleteData(url)

    suspend fun patchData(url: String, patchBody: JsonObject) =
        databaseService.patchData(url, patchBody)
}
