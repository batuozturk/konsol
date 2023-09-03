package com.batuhan.core.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject

class GetObjectListPagingSource @Inject constructor(private val cloudStorageService: CloudStorageService) :
    PagingSource<String, String>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    private var bucketName: String? = null
    private var prefix: String? = null

    override fun getRefreshKey(state: PagingState<String, String>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, String> {
        return runCatching {
            val key = params.key
            val response = cloudStorageService.getObjectList(
                bucketName!!,
                prefix = prefix,
                maxResult = PAGE_SIZE,
                pageToken = key
            )
            val prefixes = response.prefixes?.toMutableList()
            val data = prefixes?.apply {
                addAll(
                    response.items?.filter { it.name != prefix }?.map {
                        it.name ?: ""
                    } ?: listOf()
                )
            } ?: response.items?.filter { it.name != prefix }?.map {
                it.name ?: ""
            } // matchGlob cannot be applied for this case
            LoadResult.Page(
                data ?: listOf(),
                prevKey = key,
                nextKey = response.nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }

    fun setBucketName(bucketName: String) {
        this.bucketName = bucketName
    }

    fun setPrefix(prefix: String?) {
        this.prefix = prefix
    }
}
