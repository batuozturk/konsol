package com.batuhan.core.data.source.remote.management.firebase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.core.data.source.remote.management.ManagementService
import com.batuhan.core.data.model.management.ProjectInfo
import javax.inject.Inject

class AvailableProjectsPagingSource @Inject constructor(private val managementService: ManagementService) : PagingSource<String, ProjectInfo>() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, ProjectInfo>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ProjectInfo> {
        return runCatching {
            val prevKey = params.key
            val response = managementService.getAvailableProjects(prevKey, pageSize = PAGE_SIZE)
            val nextPageToken = response.nextPageToken
            val list = response.projectInfos ?: emptyList()
            LoadResult.Page(
                list,
                prevKey,
                nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }
}
