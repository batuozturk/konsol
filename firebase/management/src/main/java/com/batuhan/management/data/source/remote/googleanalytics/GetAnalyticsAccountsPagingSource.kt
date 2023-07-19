package com.batuhan.management.data.source.remote.googleanalytics

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.management.data.model.AnalyticsAccount
import javax.inject.Inject

class GetAnalyticsAccountsPagingSource @Inject constructor(private val googleAnalyticsService: GoogleAnalyticsService) :
    PagingSource<String, AnalyticsAccount>() {
    override fun getRefreshKey(state: PagingState<String, AnalyticsAccount>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AnalyticsAccount> {
        return runCatching {
            val key = params.key?.toInt() ?: 1
            val response =
                googleAnalyticsService.getAnalyticsAccounts(maxResults = 20, startIndex = key * 20)
            val startIndex = response.startIndex ?: 1
            val totalResults = response.totalResults ?: 0
            val nextKey = key + 1
            val items = response.items ?: emptyList()
            LoadResult.Page(
                data = items,
                prevKey = key.toString(),
                nextKey = if (totalResults < startIndex) null else nextKey.toString()
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }

    /*
    Pagination is not working properly,
    LoadPrams.Prepend state causes to reuse the key and crash the app,
    workaround is using the service without pagination for now (response includes at most 1000 accounts by default)
     */
}
