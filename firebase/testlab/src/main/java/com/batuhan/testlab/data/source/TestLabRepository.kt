package com.batuhan.testlab.data.source

import androidx.paging.PagingData
import com.batuhan.testlab.data.model.*
import com.batuhan.testlab.data.model.devicecatalog.TestEnvironmentCatalog
import com.batuhan.testlab.data.model.execution.Execution
import com.batuhan.testlab.data.model.execution.ExecutionEnvironment
import com.batuhan.testlab.data.model.execution.HistoryListResponse
import com.batuhan.testlab.data.model.matrix.TestMatrix
import com.batuhan.testlab.data.model.matrix.TestState
import kotlinx.coroutines.flow.Flow

interface TestLabRepository {

    suspend fun createMatrix(projectId: String, testMatrix: TestMatrix): TestMatrix

    suspend fun cancelMatrix(projectId: String, testMatrixId: String): TestState

    suspend fun getMatrix(projectId: String, testMatrixId: String): TestMatrix

    suspend fun getTestEnvironmentCatalog(projectId: String): TestEnvironmentCatalog

    suspend fun getHistoryList(projectId: String): HistoryListResponse

    fun getExecutionList(projectId: String, historyId: String): Flow<PagingData<Execution>>

    suspend fun getExecution(projectId: String, historyId: String, executionId: String): Execution

    fun getEnvironmentList(
        projectId: String,
        historyId: String,
        executionId: String
    ): Flow<PagingData<ExecutionEnvironment>>

    suspend fun getEnvironment(
        projectId: String,
        historyId: String,
        executionId: String,
        environmentId: String
    ): ExecutionEnvironment
}
