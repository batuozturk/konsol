package com.batuhan.firestore.presentation.database

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.firestore.R
import com.batuhan.core.data.model.firestore.Document
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.util.createDocumentField
import com.batuhan.firestore.presentation.database.collection.CollectionScreen
import com.batuhan.firestore.presentation.database.document.DocumentFieldBottomSheet
import com.batuhan.firestore.presentation.database.document.DocumentScreen
import com.batuhan.theme.Orange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DatabaseScreen(
    isDeleted: Boolean,
    needsRefresh: Boolean,
    onBackPressed: () -> Unit,
    navigateToCreateDocumentScreen: (String, Boolean) -> Unit,
    navigateToDeleteDocumentScreen: (String, Boolean) -> Unit,
    clearSuccessState: () -> Unit,
    clearRefreshState: () -> Unit
) {
    val viewModel = hiltViewModel<DatabaseViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val collectionIds = viewModel.collectionIds.collectAsLazyPagingItems()
    val documents = viewModel.documents.collectAsLazyPagingItems()
    val document by viewModel.document.collectAsStateWithLifecycle(initialValue = null)
    var isSuccess by remember {
        mutableStateOf(isDeleted)
    }
    val needsToRefresh by remember {
        mutableStateOf(needsRefresh)
    }
    BackHandler(enabled = true) {
        viewModel.onBackPressed()
    }
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.onBackPressed()
            isSuccess = false
            clearSuccessState.invoke()
        }
    }
    LaunchedEffect(true) {
        viewModel.databaseEvent.collect { event ->
            when (event) {
                DatabaseEvent.Back -> {
                    onBackPressed.invoke()
                }
                is DatabaseEvent.NavigateToCreateDocumentScreen -> {
                    navigateToCreateDocumentScreen.invoke(event.path, event.isCollection)
                }
                is DatabaseEvent.NavigateToDeleteDocumentScreen -> {
                    navigateToDeleteDocumentScreen.invoke(event.path, event.isCollection)
                }
            }
        }
    }
    DatabaseScreenContent(
        uiState = uiState,
        isSuccessfulDeleted = isSuccess,
        needsRefresh = needsToRefresh,
        document = document,
        documents = documents,
        collectionIds = collectionIds,
        onBackPressed = viewModel::onBackPressed,
        setErrorState = viewModel::setErrorState,
        setSnackbarState = viewModel::setSnackbarState,
        onDocumentClicked = viewModel::onDocumentClicked,
        onCollectionClicked = viewModel::onCollectionClicked,
        onDocumentRefreshed = viewModel::onDocumentRefreshed,
        setRefreshingState = viewModel::setRefreshingState,
        onRefresh = viewModel::onRefresh,
        retryOperation = viewModel::retryOperation,
        navigateToCreateDocumentScreen = viewModel::navigateToCreateDocumentScreen,
        navigateToDeleteDocumentScreen = viewModel::navigateToDeleteDocumentScreen,
        setLoadingState = viewModel::setLoadingState,
        isRootPath = viewModel::isRootPath,
        editDocumentField = viewModel::editDocumentField,
        setBottomSheetState = viewModel::setBottomSheetState,
        patchDocument = viewModel::patchDocument,
        removeDocumentField = viewModel::removeDocumentField,
        clearRefreshState = clearRefreshState,
        setDocumentFieldEdited = viewModel::setDocumentFieldEdited
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseScreenContent(
    uiState: DatabaseUiState,
    document: Document?,
    isSuccessfulDeleted: Boolean,
    needsRefresh: Boolean,
    documents: LazyPagingItems<Document>,
    collectionIds: LazyPagingItems<String>,
    onBackPressed: () -> Unit,
    setErrorState: (DatabaseErrorState) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    onDocumentClicked: (Document) -> Unit,
    onCollectionClicked: (String) -> Unit,
    onDocumentRefreshed: () -> Unit,
    setRefreshingState: (Boolean) -> Unit,
    onRefresh: (() -> Unit) -> Unit,
    retryOperation: (DatabaseErrorState, () -> Unit, () -> Unit) -> Unit,
    navigateToCreateDocumentScreen: () -> Unit,
    navigateToDeleteDocumentScreen: () -> Unit,
    setLoadingState: (Boolean) -> Unit,
    isRootPath: () -> Boolean,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    setBottomSheetState: (Boolean) -> Unit,
    patchDocument: () -> Unit,
    removeDocumentField: (Int?, Int?) -> Unit,
    clearRefreshState: () -> Unit,
    setDocumentFieldEdited: (Boolean) -> Unit
) {
    val isCollectionClicked by remember(uiState.isCollectionClicked) {
        derivedStateOf { uiState.isCollectionClicked }
    }
    val isDocumentRefreshed by remember(uiState.isDocumentRefreshed) {
        derivedStateOf { uiState.isDocumentRefreshed }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isRefreshing by remember(uiState.isRefreshing) {
        derivedStateOf { uiState.isRefreshing }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val currentPath by remember(uiState.currentPath) {
        derivedStateOf { uiState.currentPath }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false }
    )
    val isBottomSheetOpened by remember(uiState.isBottomSheetOpened) {
        derivedStateOf { uiState.isBottomSheetOpened }
    }
    val currentEditedFieldParentIndex by remember(uiState.currentEditedFieldParentIndex) {
        derivedStateOf { uiState.currentEditedFieldParentIndex }
    }
    val currentEditedFieldIndex by remember(uiState.currentEditedFieldIndex) {
        derivedStateOf { uiState.currentEditedFieldIndex }
    }
    val editableField by remember(uiState.currentEditedField) {
        derivedStateOf { uiState.currentEditedField }
    }
    val isDocumentFieldEditing by remember(uiState.isDocumentFieldEditing) {
        derivedStateOf { uiState.isDocumentFieldEditing }
    }
    val coroutineScope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    val context = LocalContext.current

    LaunchedEffect(true) {
        needsRefresh.takeIf { it }?.let {
            setRefreshingState.invoke(true)
            onRefresh.invoke {
                if (isCollectionClicked) {
                    coroutineScope.launch {
                        delay(1L) // workaround for not refreshing problem
                        documents.refresh()
                    }
                } else {
                    coroutineScope.launch {
                        delay(1L) // workaround for not refreshing problem
                        collectionIds.refresh()
                    }
                }
            }
            clearRefreshState.invoke()
        }
    }
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val titleText = context.getString(it)
            val actionText = errorState?.actionResId?.let { resId -> context.getString(resId) }
            val result = snackbarHostState.showSnackbar(
                message = titleText,
                actionLabel = actionText,
                withDismissAction = actionText == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    retryOperation(
                        errorState!!,
                        { documents.refresh() },
                        { collectionIds.refresh() }
                    )
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.Dismissed -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    if (!isSuccessfulDeleted) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(
                        snackbarData = it,
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        actionColor = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onBackPressed.invoke()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                    },
                    title = {
                        Text(
                            stringResource(
                                id = if (isRootPath()) R.string.root_path
                                else if (isCollectionClicked) R.string.collection
                                else R.string.document
                            )
                        )
                    },
                    actions = {
                        if (errorState != null) {
                            IconButton(
                                onClick = {
                                    setSnackbarState.invoke(true)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        } else if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(24.dp),
                                strokeWidth = 2.dp,
                                color = Orange,
                                trackColor = Color.White
                            )
                        } else {
                            if (!isRootPath() && !isCollectionClicked) {
                                IconButton(
                                    onClick = navigateToDeleteDocumentScreen
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Orange
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        setDocumentFieldEdited.invoke(false)
                                        editDocumentField.invoke(
                                            createDocumentField(
                                                DocumentField.Type.STRING
                                            ),
                                            null,
                                            null
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Orange
                                    )
                                }
                            }
                            IconButton(
                                onClick = navigateToCreateDocumentScreen
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Text(currentPath ?: "", modifier = Modifier.padding(8.dp))
                SwipeRefresh(
                    modifier = Modifier.fillMaxSize(),
                    state = swipeRefreshState,
                    onRefresh = {
                        setSnackbarState.invoke(false)
                        snackbarHostState.currentSnackbarData?.dismiss()
                        setRefreshingState.invoke(true)
                        onRefresh.invoke {
                            if (isCollectionClicked) documents.refresh()
                            else {
                                onDocumentRefreshed.invoke()
                                collectionIds.refresh()
                            }
                        }
                    }
                ) {
                    if (isCollectionClicked) {
                        CollectionScreen(
                            documents = documents,
                            setErrorState = setErrorState,
                            setLoadingState = setLoadingState,
                            onDocumentClicked = onDocumentClicked
                        )
                    } else {
                        DocumentScreen(
                            document = document,
                            isRoot = isRootPath.invoke(),
                            isLoading = isLoading,
                            collectionIds = collectionIds,
                            setLoadingState = setLoadingState,
                            setErrorState = setErrorState,
                            onCollectionIdClicked = onCollectionClicked,
                            editDocumentField = { field, index, parentIndex ->
                                editDocumentField.invoke(field, index, parentIndex)
                            },
                            removeDocumentField = removeDocumentField,
                            setEditingState = setDocumentFieldEdited
                        )
                    }
                }
            }
        }
        if (isBottomSheetOpened) {
            DocumentFieldBottomSheet(
                isEditing = isDocumentFieldEditing,
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                setBottomSheetState = setBottomSheetState,
                editDocumentField = editDocumentField,
                field = editableField,
                parentDocumentFieldIndex = currentEditedFieldParentIndex,
                fieldIndex = currentEditedFieldIndex,
                onSave = patchDocument
            )
        }
    }
}
