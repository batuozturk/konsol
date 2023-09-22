package com.batuhan.firestore.presentation.database.collection

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.batuhan.firestore.R
import com.batuhan.core.data.model.firestore.Document
import com.batuhan.firestore.presentation.database.DatabaseErrorState
import com.batuhan.theme.Orange

@Composable
fun CollectionScreen(
    documents: LazyPagingItems<Document>,
    setErrorState: (DatabaseErrorState) -> Unit,
    onDocumentClicked: (Document) -> Unit,
    setLoadingState: (Boolean) -> Unit
) {
    when (documents.loadState.refresh) {
        is LoadState.Error -> {
            setErrorState.invoke(DatabaseErrorState.LIST_COLLECTION_IDS)
            setLoadingState.invoke(false)
        }
        else -> {
            setLoadingState.invoke(false)
        }
    }

    when (documents.loadState.append) {
        is LoadState.Error -> {
            setErrorState.invoke(DatabaseErrorState.LIST_COLLECTION_IDS)
        }
        else -> {
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(documents.itemCount) {
            documents[it]?.let { document ->
                DocumentItem(
                    document = document,
                    onDocumentClicked = onDocumentClicked
                )
            }
        }
    }
}

@Composable
fun DocumentItem(document: Document, onDocumentClicked: (Document) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp)).clickable {
                onDocumentClicked.invoke(document)
            }.padding(horizontal = 10.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            document.name?.substring(document.name!!.lastIndexOf("/") + 1)
                ?: stringResource(id = R.string.undefined)
        )
    }
}
