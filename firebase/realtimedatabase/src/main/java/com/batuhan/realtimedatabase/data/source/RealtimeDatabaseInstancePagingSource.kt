package com.batuhan.realtimedatabase.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import javax.inject.Inject

class RealtimeDatabaseInstancePagingSource @Inject constructor(private val service: RealtimeDatabaseInstanceService) :
    PagingSource<String, DatabaseInstance>() {

    private var projectId: String? = null

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, DatabaseInstance>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, DatabaseInstance> {
        return runCatching {
            val key = params.key
            val response = service.getDatabaseInstances(projectId!!, key, PAGE_SIZE)
            LoadResult.Page(
                response.instances ?: listOf(),
                key,
                response.nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }

    fun setProjectId(projectId: String) {
        this.projectId = projectId
    }
}
