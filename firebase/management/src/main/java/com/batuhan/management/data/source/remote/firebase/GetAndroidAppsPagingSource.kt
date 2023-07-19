package com.batuhan.management.data.source.remote.firebase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.management.data.model.AndroidApp
import javax.inject.Inject

class GetAndroidAppsPagingSource @Inject constructor(private val managementService: ManagementService) :
    PagingSource<String, AndroidApp>() {

    private var projectId: String? = null

    companion object {
        const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, AndroidApp>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AndroidApp> {
        return runCatching {
            val key = params.key
            val response = managementService.getAndroidApps(
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
