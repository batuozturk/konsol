package com.batuhan.testlab.data.source.result

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.testlab.data.model.execution.Execution
import javax.inject.Inject

class GetExecutionListPagingSource @Inject constructor(private val testLabToolResultsService: TestLabToolResultsService) :
    PagingSource<String, Execution>() {

    private var projectId: String? = null
    private var historyId: String? = null

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, Execution>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Execution> {
        return runCatching {
            val key = params.key
            val response =
                testLabToolResultsService.getExecutionList(projectId!!, historyId!!, PAGE_SIZE, key)
            LoadResult.Page(
                data = response.executions ?: listOf(),
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
}
