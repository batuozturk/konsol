package com.batuhan.core.data.source.remote.firestore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.batuhan.core.data.model.firestore.Document
import javax.inject.Inject

class ListDocumentsPagingSource @Inject constructor(private val firestoreService: FirestoreService) :
    PagingSource<String, Document>() {

    private var parent: String? = null
    private var collectionId: String? = null

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<String, Document>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Document> {
        return runCatching {
            val key = params.key
            val response = firestoreService.listDocuments(parent!!, collectionId!!, PAGE_SIZE, key)
            LoadResult.Page(
                data = response.documents ?: listOf(),
                prevKey = key,
                nextKey = response.nextPageToken
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }

    fun setAttributes(parent: String, collectionId: String) {
        this.parent = parent
        this.collectionId = collectionId
    }
}
