package com.batuhan.core.data.source.remote.firestore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject

class ListCollectionIdsPagingSource @Inject constructor(private val firestoreService: FirestoreService) :
    PagingSource<String, String>() {

    private var parent: String? = null

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, String>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, String> {
        return runCatching {
            val key = params.key
            val response = firestoreService.listCollectionIds(parent!!, PAGE_SIZE, key)
            LoadResult.Page(
                data = response.collectionIds ?: listOf(),
                prevKey = key,
                nextKey = response.nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }

    fun setParent(parent: String) {
        this.parent = parent
    }
}
