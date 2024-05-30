package com.batuhan.testlab.presentation.selectfile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.testlab.R
import com.batuhan.theme.Orange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SelectFileScreen(
    onBackPressed: () -> Unit,
    onSuccess: (gcsPath: String) -> Unit
) {
    val viewModel = hiltViewModel<SelectFileViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val objects = viewModel.objects.collectAsLazyPagingItems()
    LaunchedEffect(true) {
        viewModel.selectFileEvent.collect { event ->
            when (event) {
                SelectFileEvent.Back -> onBackPressed.invoke()
                is SelectFileEvent.Success -> onSuccess.invoke(event.gcsPath)
            }
        }
    }
    BackHandler {
        viewModel.onBackPressed()
    }
    SelectFileScreenContent(
        uiState = uiState,
        objects = objects,
        onBackPressed = viewModel::onBackPressed,
        setErrorState = viewModel::setErrorState,
        setSelectedFile = viewModel::setSelectedFile,
        clearSelectedFile = viewModel::clearSelectedFile,
        setSnackbarState = viewModel::setSnackbarState,
        onRefresh = viewModel::setRefreshState,
        retryOperation = viewModel::retryOperation,
        setPrefix = viewModel::setPrefix,
        onSave = viewModel::onSave,
        setRefreshingState = viewModel::setRefreshingState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectFileScreenContent(
    uiState: SelectFileUiState,
    objects: LazyPagingItems<String>,
    onBackPressed: () -> Unit,
    setErrorState: (SelectFileErrorState) -> Unit,
    setSelectedFile: (String) -> Unit,
    clearSelectedFile: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    onRefresh: (() -> Unit) -> Unit,
    retryOperation: (SelectFileErrorState, () -> Unit) -> Unit,
    setPrefix: (String) -> Unit,
    onSave: () -> Unit,
    setRefreshingState: (Boolean) -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val isRefreshing by remember(uiState.isRefreshing) {
        derivedStateOf { uiState.isRefreshing }
    }
    val selectedFileName by remember(uiState.selectedFileName) {
        derivedStateOf { uiState.selectedFileName }
    }
    val context = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
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
                    retryOperation.invoke(errorState!!) {
                        objects.refresh()
                    }
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
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    shape = RoundedCornerShape(10.dp),
                    snackbarData = it,
                    actionColor = Color.White,
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.select_file))
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
                    } else {
                        IconButton(onSave) {
                            Icon(
                                imageVector = Icons.Default.Save,
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
            selectedFileName?.let {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                        .border(2.dp, Orange, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(7f),
                        text = it
                    )
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            clearSelectedFile.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            tint = Orange,
                            contentDescription = null
                        )
                    }
                }
            }
            SwipeRefresh(
                modifier = Modifier.fillMaxSize(),
                state = swipeRefreshState,
                onRefresh = {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                    setRefreshingState.invoke(true)
                    onRefresh.invoke {
                        objects.refresh()
                    }
                }
            ) {
                when (objects.loadState.refresh) {
                    is LoadState.Error -> {
                        setErrorState.invoke(SelectFileErrorState.CLOUD_STORAGE)
                    }
                    else -> {
                    }
                }

                when (objects.loadState.append) {
                    is LoadState.Error -> {
                        setErrorState.invoke(SelectFileErrorState.CLOUD_STORAGE)
                    }
                    else -> {
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(objects.itemCount) {
                        CloudStorageObjectItem(
                            name = objects[it],
                            isAlreadyAdded = uiState.selectedFileName != null,
                            setSelectedFile = setSelectedFile,
                            setPrefix = setPrefix
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CloudStorageObjectItem(
    name: String?,
    isAlreadyAdded: Boolean,
    setSelectedFile: (String) -> Unit,
    setPrefix: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(10.dp).fillMaxWidth()
            .clickable {
                setPrefix.invoke(name.takeIf { it?.last() == '/' } ?: return@clickable)
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = if (isAlreadyAdded) 20.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = 4.dp).weight(7f),
            text = name ?: stringResource(id = R.string.undefined)
        )
        if (!isAlreadyAdded) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    setSelectedFile.invoke(name ?: return@IconButton)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    tint = Orange,
                    contentDescription = null
                )
            }
        }
    }
}
