package com.batuhan.core.data.source.remote.firestore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.core.data.model.firestore.FirestoreLocation
import javax.inject.Inject

class ListFirestoreLocationsPagingSource @Inject constructor(private val firestoreService: FirestoreService) :
    PagingSource<String, FirestoreLocation>() {

    private var projectId: String? = null

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, FirestoreLocation>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, FirestoreLocation> {
        return runCatching {
            val key = params.key
            val response = firestoreService.listFirestoreLocations(projectId!!, PAGE_SIZE, key)
            LoadResult.Page(
                data = response.locations ?: listOf(),
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
