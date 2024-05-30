package com.batuhan.realtimedatabase.data.repository

import androidx.paging.PagingData
import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseInstanceRepository {

    suspend fun createDatabaseInstance(
        projectId: String,
        locationId: String,
        databaseId: String,
        databaseInstance: DatabaseInstance
    ): DatabaseInstance

    fun getDatabaseInstanceList(projectId: String): Flow<PagingData<DatabaseInstance>>

    suspend fun getDatabase(url: String): JsonElement?

    suspend fun deleteData(url: String)

    suspend fun patchData(url: String, patchBody: JsonObject)
}
