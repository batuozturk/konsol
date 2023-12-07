package com.batuhan.realtimedatabase.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import com.batuhan.realtimedatabase.data.source.RealtimeDatabaseInstanceDataSource
import com.batuhan.realtimedatabase.data.source.RealtimeDatabaseInstancePagingSource
import com.batuhan.realtimedatabase.data.source.RealtimeDatabaseInstanceService
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealtimeDatabaseInstanceRepositoryImpl @Inject constructor(
    private val dataSource: RealtimeDatabaseInstanceDataSource,
    private val service: RealtimeDatabaseInstanceService
) :
    RealtimeDatabaseInstanceRepository {
    override suspend fun createDatabaseInstance(
        projectId: String,
        locationId: String,
        databaseId: String,
        databaseInstance: DatabaseInstance
    ): DatabaseInstance {
        return dataSource.createDatabaseInstance(
            projectId,
            locationId,
            databaseId,
            databaseInstance
        )
    }

    override fun getDatabaseInstanceList(projectId: String): Flow<PagingData<DatabaseInstance>> {
        return Pager(
            config = PagingConfig(20, enablePlaceholders = false),
            pagingSourceFactory = {
                RealtimeDatabaseInstancePagingSource(service).apply {
                    setProjectId(projectId)
                }
            }
        ).flow
    }

    override suspend fun getDatabase(url: String): JsonElement? {
        return dataSource.getDatabase(url)
    }

    override suspend fun deleteData(url: String) {
        dataSource.deleteData(url)
    }

    override suspend fun patchData(url: String, patchBody: JsonObject){
        return dataSource.patchData(url, patchBody)
    }
}
