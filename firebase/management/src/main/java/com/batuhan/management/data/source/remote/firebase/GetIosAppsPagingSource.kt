package com.batuhan.management.data.source.remote.firebase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.management.data.model.IosApp
import javax.inject.Inject

class GetIosAppsPagingSource @Inject constructor(private val managementService: ManagementService) :
    PagingSource<String, IosApp>() {

    private var projectId: String? = null

    companion object {
        const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, IosApp>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, IosApp> {
        return runCatching {
            val key = params.key
            val response = managementService.getIosApps(
                projectId = projectId!!,
                pageToken = key,
                pageSize = PAGE_SIZE
            )
            LoadResult.Page(
                data = response.apps ?: listOf(),
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
}
