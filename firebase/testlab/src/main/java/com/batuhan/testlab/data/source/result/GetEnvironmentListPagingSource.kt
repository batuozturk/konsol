package com.batuhan.testlab.data.source.result

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.testlab.data.model.execution.ExecutionEnvironment
import javax.inject.Inject

class GetEnvironmentListPagingSource @Inject constructor(private val testLabToolResultsService: TestLabToolResultsService) :
    PagingSource<String, ExecutionEnvironment>() {

    private var projectId: String? = null
    private var historyId: String? = null
    private var executionId: String? = null

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, ExecutionEnvironment>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ExecutionEnvironment> {
        return runCatching {
            val key = params.key
            val response =
                testLabToolResultsService.getEnvironmentList(
                    projectId!!,
                    historyId!!,
                    executionId!!,
                    PAGE_SIZE,
                    key
                )
            LoadResult.Page(
                data = response.environments ?: listOf(),
                prevKey = key,
                nextKey = response.nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }

    fun setProjectId(projectId: String) {
        this.projectId = projectId
    }

    fun setHistoryId(historyId: String) {
        this.historyId = historyId
    }

    fun setExecutionId(executionId: String) {
        this.executionId = executionId
    }
}
