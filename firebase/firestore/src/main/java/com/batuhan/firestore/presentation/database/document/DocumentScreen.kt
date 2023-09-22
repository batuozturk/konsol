package com.batuhan.firestore.presentation.database.document

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.batuhan.firestore.R
import com.batuhan.core.data.model.firestore.Document
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.data.model.toDocumentFieldList
import com.batuhan.firestore.presentation.database.DatabaseErrorState
import com.batuhan.firestore.presentation.database.document.fields.bytype.DocumentFieldItemByType
import com.batuhan.theme.Orange
import com.batuhan.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DocumentScreen(
    document: Document?,
    isLoading: Boolean,
    isRoot: Boolean,
    collectionIds: LazyPagingItems<String>,
    setLoadingState: (Boolean) -> Unit,
    setErrorState: (DatabaseErrorState) -> Unit,
    onCollectionIdClicked: (String) -> Unit,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    removeDocumentField: (Int?, Int?) -> Unit,
    setEditingState: (Boolean) -> Unit
) {
    when (collectionIds.loadState.refresh) {
        is LoadState.Error -> {
            setErrorState.invoke(DatabaseErrorState.LIST_COLLECTION_IDS)
            setLoadingState.invoke(false)
        }
        else -> {
            setLoadingState.invoke(false)
        }
    }

    when (collectionIds.loadState.append) {
        is LoadState.Error -> {
            setErrorState.invoke(DatabaseErrorState.LIST_COLLECTION_IDS)
        }
        else -> {
        }
    }
    if (isRoot) {
        CollectionList(
            collectionIds = collectionIds,
            onCollectionIdClicked = onCollectionIdClicked
        )
    } else {
        val pagerState = rememberPagerState { 2 }
        var selectedTabIndex by remember {
            mutableStateOf(pagerState.currentPage)
        }
        val coroutineScope = rememberCoroutineScope()
        val tabTitles = listOf(R.string.document_info, R.string.collection_id_list)
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                tabs = {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = {
                                selectedTabIndex = index
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = stringResource(id = title),
                                    style = Typography.bodyLarge
                                )
                            }
                        )
                    }
                },
                divider = {
                },
                indicator = {
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(it[selectedTabIndex])
                            .padding(horizontal = 16.dp),
                        color = Orange,
                        height = 4.dp
                    )
                },
                contentColor = Color.Black
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) {
                when (it) {
                    0 -> if (!isLoading) DocumentInfoItem(
                        document = document,
                        editDocumentField = editDocumentField,
                        removeDocumentField = removeDocumentField,
                        setEditingState = setEditingState
                    )
                    1 -> CollectionList(
                        collectionIds = collectionIds,
                        onCollectionIdClicked = onCollectionIdClicked
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentInfoItem(
    document: Document?,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    removeDocumentField: (Int?, Int?) -> Unit,
    setEditingState: (Boolean) -> Unit
) {
    val documentFieldList = document?.fields?.toDocumentFieldList() ?: listOf()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(documentFieldList.size) {
            DocumentFieldItemByType(
                field = documentFieldList[it],
                fieldIndex = it,
                editDocumentField = editDocumentField,
                removeDocumentField = removeDocumentField,
                setEditingState = setEditingState
            )
        }
    }
}

@Composable
fun CollectionList(
    collectionIds: LazyPagingItems<String>,
    onCollectionIdClicked: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(collectionIds.itemCount) {
            collectionIds[it]?.let { collectionId ->
                CollectionIdItem(
                    collectionId = collectionId,
                    onCollectionIdClicked = onCollectionIdClicked
                )
            }
        }
    }
}

@Composable
fun CollectionIdItem(collectionId: String, onCollectionIdClicked: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                onCollectionIdClicked.invoke(collectionId)
            }
            .padding(horizontal = 10.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(collectionId)
    }
}
