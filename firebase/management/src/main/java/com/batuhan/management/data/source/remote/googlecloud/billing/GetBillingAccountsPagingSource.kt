package com.batuhan.management.data.source.remote.googlecloud.billing

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.management.data.model.BillingAccount
import javax.inject.Inject

class GetBillingAccountsPagingSource @Inject constructor(private val googleCloudBillingService: GoogleCloudBillingService) :
    PagingSource<String, BillingAccount>() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, BillingAccount>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, BillingAccount> {
        return runCatching {
            val key = if (params.key?.isEmpty() == true) null else params.key
            val response =
                googleCloudBillingService.getBillingAccounts(pageSize = PAGE_SIZE, pageToken = key)
            val list = response.billingAccounts ?: emptyList()
            val nextPageToken =
                if (response.nextPageToken?.isEmpty() == true) null else response.nextPageToken
            LoadResult.Page(
                data = list,
                prevKey = key,
                nextKey = nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }
}
