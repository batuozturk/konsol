package com.batuhan.core.data.source.remote.management.firebase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.data.source.remote.management.ManagementService
import javax.inject.Inject

class ProjectsPagingSource @Inject constructor(private val service: ManagementService) :
    PagingSource<String, FirebaseProject>() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, FirebaseProject>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, FirebaseProject> {
        return runCatching {
            val page = params.key
            val response = service.getProjects(pageToken = page, pageSize = PAGE_SIZE)
            val items = response.results ?: emptyList()
            LoadResult.Page(
                data = items,
                prevKey = page,
                nextKey = response.nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }
}
