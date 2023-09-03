package com.batuhan.testlab.data.source.result

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.batuhan.testlab.data.model.execution.Execution
import com.batuhan.testlab.data.model.execution.ExecutionEnvironment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TestLabToolResultsDataSource @Inject constructor(private val testLabToolResultsService: TestLabToolResultsService) {

    companion object {
        private const val PAGE_SIZE = 20
    }

    suspend fun getHistoryList(projectId: String) =
        testLabToolResultsService.getHistoryList(projectId)

    fun getExecutionList(
        projectId: String,
        historyId: String
    ): Flow<PagingData<Execution>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = {
            GetExecutionListPagingSource(testLabToolResultsService).apply {
                setProjectId(projectId)
                setHistoryId(historyId)
            }
        }
    ).flow

    suspend fun getExecution(
        projectId: String,
        historyId: String,
        executionId: String
    ) = testLabToolResultsService.getExecution(projectId, historyId, executionId)

    fun getEnvironmentList(
        projectId: String,
        historyId: String,
        executionId: String
    ): Flow<PagingData<ExecutionEnvironment>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = {
            GetEnvironmentListPagingSource(testLabToolResultsService).apply {
                setProjectId(projectId)
                setHistoryId(historyId)
                setExecutionId(executionId)
            }
        }
    ).flow

    suspend fun getEnvironment(
        projectId: String,
        historyId: String,
        executionId: String,
        environmentId: String
    ) = testLabToolResultsService.getEnvironment(projectId, historyId, executionId, environmentId)
}
