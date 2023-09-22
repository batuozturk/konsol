package com.batuhan.core.data.source.remote.management.firebase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.core.data.model.management.AvailableLocation
import com.batuhan.core.data.source.remote.management.ManagementService
import javax.inject.Inject

class AvailableLocationsPagingSource @Inject constructor(private val managementService: ManagementService) :
    PagingSource<String, AvailableLocation>() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private var projectName: String? = null

    override fun getRefreshKey(state: PagingState<String, AvailableLocation>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AvailableLocation> {
        return runCatching {
            val prevKey = params.key
            val response = managementService.getAvailableLocations(
                projectName!!,
                pageSize = PAGE_SIZE,
                pageToken = prevKey
            )
            val list = response.locations ?: emptyList()
            LoadResult.Page(
                list,
                prevKey,
                response.nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }

    fun setProjectName(projectName: String) {
        this.projectName = projectName
    }
}
