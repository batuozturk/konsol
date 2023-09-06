package com.batuhan.cloudstorage.presentation.objectlist

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.batuhan.cloudstorage.R
import com.batuhan.theme.Orange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun ObjectListScreen(
    onBackPressed: () -> Unit,
    navigateToCreateFolderScreen: (String, String?) -> Unit
) {
    val viewModel = hiltViewModel<ObjectListViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val objects = viewModel.objects.collectAsLazyPagingItems()
    LaunchedEffect(true) {
        viewModel.selectFileEvent.collect { event ->
            when (event) {
                ObjectListEvent.Back -> onBackPressed.invoke()
                is ObjectListEvent.CreateFolder -> navigateToCreateFolderScreen.invoke(
                    event.bucketName,
                    event.prefix
                )
            }
        }
    }
    val context = LocalContext.current
    val activityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            viewModel.uploadFile(
                context.contentResolver,
                it ?: return@rememberLauncherForActivityResult
            )
        }
    )
    BackHandler {
        viewModel.onBackPressed()
    }
    ObjectListScreenContent(
        uiState = uiState,
        objects = objects,
        onBackPressed = viewModel::onBackPressed,
        uploadFile = {
            activityResult.launch(arrayOf("*/*"))
        },
        setErrorState = viewModel::setErrorState,
        setSnackbarState = viewModel::setSnackbarState,
        setLoadingState = viewModel::setLoadingState,
        onRefresh = viewModel::setRefreshState,
        retryOperation = viewModel::retryOperation,
        setRefreshingState = viewModel::setRefreshingState,
        setPrefix = viewModel::setPrefix,
        navigateToCreateFolderScreen = viewModel::navigateToCreateFolder,
        deleteFile = viewModel::deleteFile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectListScreenContent(
    uiState: ObjectListUiState,
    objects: LazyPagingItems<String>,
    onBackPressed: () -> Unit,
    uploadFile: () -> Unit,
    setErrorState: (ObjectListErrorState) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    setLoadingState: (Boolean) -> Unit,
    onRefresh: (() -> Unit) -> Unit,
    retryOperation: (ObjectListErrorState, () -> Unit) -> Unit,
    setRefreshingState: (Boolean) -> Unit,
    setPrefix: (String) -> Unit,
    navigateToCreateFolderScreen: () -> Unit,
    deleteFile: (String) -> Unit
) {
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val isRefreshing by remember(uiState.isRefreshing) {
        derivedStateOf { uiState.isRefreshing }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val prefix by remember(uiState.prefix) {
        derivedStateOf { uiState.prefix }
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
                    retryOperation(errorState!!) {
                        onRefresh {
                            objects.refresh()
                        }
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
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    contentColor = Color.White,
                    containerColor = Color.Red,
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
                    Text("Object List")
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
                            modifier = Modifier.padding(horizontal = 12.dp).size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else {
                        IconButton(
                            onClick = {
                                navigateToCreateFolderScreen.invoke()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                        IconButton(
                            onClick = {
                                uploadFile.invoke()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            Text("/" + (prefix ?: ""), modifier = Modifier.padding(8.dp))
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
                        setErrorState.invoke(ObjectListErrorState.CLOUD_STORAGE)
                    }
                    else -> {
                    }
                }

                when (objects.loadState.append) {
                    is LoadState.Error -> {
                        setErrorState.invoke(ObjectListErrorState.CLOUD_STORAGE)
                    }
                    else -> {
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(objects.itemCount) {
                        CloudStorageObjectItem(
                            name = objects[it],
                            setPrefix = setPrefix,
                            deleteFile = deleteFile
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
    setPrefix: (String) -> Unit,
    deleteFile: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth()
            .clickable {
                setPrefix.invoke(name.takeIf { it?.last() == '/' } ?: return@clickable)
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = 4.dp).weight(7f),
            text = name.takeIf { it?.last() == '/' }?.let { name }
                ?: name?.substring(name.lastIndexOf('/') + 1)
                ?: stringResource(id = R.string.undefined)
        )
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { deleteFile.invoke(name ?: return@IconButton) }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}
