package com.batuhan.testlab.presentation.resultlist

import androidx.compose.animation.AnimatedVisibility
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
import com.batuhan.testlab.data.model.execution.Execution
import com.batuhan.testlab.data.model.execution.ExecutionState
import com.batuhan.theme.Orange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun ResultListScreen(
    onBackPressed: () -> Unit,
    navigateToCreateMatrixScreen: (String) -> Unit,
    navigateToExecutionDetailScreen: (String, String, String) -> Unit
) {
    val viewModel = hiltViewModel<ResultListViewModel>()
    val executionList = viewModel.executionList.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.resultListEvent.collect { event ->
            when (event) {
                ResultListEvent.Back -> onBackPressed.invoke()
                is ResultListEvent.CreateMatrix -> navigateToCreateMatrixScreen.invoke(event.projectId)
                is ResultListEvent.ExecutionDetail -> navigateToExecutionDetailScreen.invoke(
                    event.projectId,
                    event.historyId,
                    event.executionId
                )
            }
        }
    }
    ResultListScreenContent(
        uiState = uiState,
        executionList = executionList,
        onBackPressed = viewModel::onBackPressed,
        navigateToCreateMatrixScreen = viewModel::navigateToCreateMatrixScreen,
        navigateToExecutionDetailScreen = viewModel::navigateToExecutionDetailScreen,
        setErrorState = viewModel::setErrorState,
        retryOperation = viewModel::retryOperation,
        setSnackbarState = viewModel::setSnackbarState,
        onRefresh = viewModel::onRefresh,
        getHistoryList = viewModel::getHistoryList
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultListScreenContent(
    uiState: ResultListUiState,
    executionList: LazyPagingItems<Execution>,
    onBackPressed: () -> Unit,
    navigateToCreateMatrixScreen: () -> Unit,
    navigateToExecutionDetailScreen: (String) -> Unit,
    setErrorState: (ResultListErrorState) -> Unit,
    retryOperation: (ResultListErrorState, (() -> Unit)?) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    onRefresh: (() -> Unit) -> Unit,
    getHistoryList: () -> Unit
) {
    val isRefreshing by remember(uiState.isRefreshing) {
        derivedStateOf { uiState.isRefreshing }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val title = context.getString(it)
            val actionTitle = errorState?.actionResId?.let { resId -> context.getString(resId) }
            when (
                snackbarHostState.showSnackbar(
                    title,
                    withDismissAction = actionTitle == null,
                    actionLabel = actionTitle,
                    duration = SnackbarDuration.Indefinite
                )
            ) {
                SnackbarResult.ActionPerformed -> {
                    retryOperation.invoke(errorState!!) {
                        executionList.refresh()
                    }
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.Dismissed -> {
                    retryOperation.invoke(errorState!!, null)
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
                            id = R.string.test_result_list
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
                    } else {
                        IconButton(
                            onClick = navigateToCreateMatrixScreen
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
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                setSnackbarState.invoke(false)
                snackbarHostState.currentSnackbarData?.dismiss()
                onRefresh.invoke {
                    getHistoryList.invoke()
                    executionList.refresh()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (executionList.loadState.refresh) {
                is LoadState.Error -> {
                    setErrorState.invoke(ResultListErrorState.RESULT_LIST)
                }
                else -> {
                }
            }

            when (executionList.loadState.append) {
                is LoadState.Error -> {
                    setErrorState.invoke(ResultListErrorState.RESULT_LIST)
                }
                else -> {
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(executionList.itemCount) { index ->
                    executionList[index]?.let { execution ->
                        ExecutionItem(
                            execution = execution,
                            navigateToExecutionDetailScreen = navigateToExecutionDetailScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutionItem(execution: Execution, navigateToExecutionDetailScreen: (String) -> Unit) {
    var isOpened by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navigateToExecutionDetailScreen.invoke(
                    execution.executionId ?: return@clickable
                )
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(7f)
        ) {
            Text(execution.textExecutionMatrixId ?: "")
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                if (execution.specification?.androidTest != null) "android"
                else if (execution.specification?.iosTest != null) "ios"
                else "undefined"
            )
        }
        if (execution.state == ExecutionState.inProgress || execution.state == ExecutionState.pending) {
            CircularProgressIndicator(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .size(24.dp),
                strokeWidth = 2.dp,
                color = Orange,
                trackColor = Color.White
            )
        } else if (execution.state == ExecutionState.complete && execution.outcome?.summary != null) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = execution.outcome.summary.imageVector,
                contentDescription = null,
                tint = execution.outcome.summary.tint
            )
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { isOpened = !isOpened }
        ) {
            Icon(
                imageVector = if (isOpened) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Orange
            )
        }
    }
    AnimatedVisibility(isOpened) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(start = 16.dp)
                .border(2.dp, Orange, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Text(execution.specification?.androidTest?.androidAppInfo?.packageName ?: "")
            Spacer(modifier = Modifier.height(10.dp))
            Text(execution.specification?.androidTest?.androidAppInfo?.versionName ?: "")
        }
    }
}
