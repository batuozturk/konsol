package com.batuhan.testlab.presentation.execution

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.batuhan.testlab.data.model.execution.ExecutionEnvironment
import com.batuhan.testlab.data.model.execution.Outcome
import com.batuhan.theme.Orange
import com.batuhan.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun ExecutionScreen(
    onBackPressed: () -> Unit,
    navigateToEnvironmentScreen: (String, String, String, String) -> Unit
) {
    val viewModel = hiltViewModel<ExecutionViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val environments = viewModel.environments.collectAsLazyPagingItems()
    LaunchedEffect(true) {
        viewModel.executionEvent.collect { event ->
            when (event) {
                ExecutionEvent.Back -> onBackPressed.invoke()
                is ExecutionEvent.NavigateToEnvironmentScreen -> navigateToEnvironmentScreen.invoke(
                    event.projectId,
                    event.historyId,
                    event.executionId,
                    event.environmentId
                )
            }
        }
    }
    ExecutionScreenContent(
        uiState = uiState,
        environments = environments,
        onBackPressed = viewModel::onBackPressed,
        retryOperation = viewModel::retryOperation,
        setSnackbarState = viewModel::setSnackbarState,
        setErrorState = viewModel::setErrorState,
        navigateToEnvironmentScreen = viewModel::navigateToEnvironmentScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExecutionScreenContent(
    uiState: ExecutionUiState,
    environments: LazyPagingItems<ExecutionEnvironment>,
    onBackPressed: () -> Unit,
    retryOperation: (ExecutionErrorState, (() -> Unit)?) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    setErrorState: (ExecutionErrorState) -> Unit,
    navigateToEnvironmentScreen: (String) -> Unit
) {
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val execution by remember(uiState.execution) {
        derivedStateOf { uiState.execution }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
    val tabTitles = listOf(R.string.execution_details, R.string.execution_environments)
    val pagerState = rememberPagerState { tabTitles.size }
    var selectedTabIndex by remember {
        mutableStateOf(pagerState.currentPage)
    }
    val coroutineScope = rememberCoroutineScope()
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
                        environments.refresh()
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
                        execution?.textExecutionMatrixId ?: ""
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
                                .padding(horizontal = 16.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
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
            ) { page ->
                when (page) {
                    0 -> ExecutionDetail(execution = execution)
                    1 -> EnvironmentList(
                        environments = environments,
                        setErrorState = setErrorState,
                        navigateToEnvironmentScreen = navigateToEnvironmentScreen
                    )
                }
            }
        }
    }
}

@Composable
fun ExecutionDetail(execution: Execution?) {
    execution?.run {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ExecutionItem(
                    title = stringResource(id = R.string.matrix_id),
                    value = textExecutionMatrixId ?: stringResource(R.string.undefined)
                )
            }
            item {
                ExecutionItem(
                    title = stringResource(id = R.string.execution_id),
                    value = executionId ?: stringResource(R.string.undefined)
                )
            }
            item {
                ExecutionItem(
                    title = stringResource(id = R.string.creation_date),
                    value = creationTime?.getDate() ?: stringResource(R.string.undefined)
                )
            }
            item {
                ExecutionItem(
                    title = stringResource(id = R.string.completion_date),
                    value = completionTime?.getDate() ?: stringResource(R.string.undefined)
                )
            }
            outcome?.summary?.let {
                item {
                    ExecutionItem(
                        title = stringResource(id = R.string.test_summary),
                        value = it.name
                    )
                }
            }
            specification?.androidTest?.let {
                val title = when {
                    it.androidRoboTest != null -> "Android Robo Test"
                    it.androidInstrumentationTest != null -> "Android Instrumentation Test"
                    it.androidTestLoop != null -> "Android Test Loop"
                    else -> null
                }
                title?.let {
                    item {
                        ExecutionItem(
                            title = stringResource(id = R.string.test_type),
                            value = title
                        )
                    }
                }
            }
            specification?.iosTest?.let {
                val title = when {
                    it.iosXcTest != null -> "iOS XCTest"
                    it.iosRoboTest != null -> "iOS Robo Test"
                    it.iosTestLoop != null -> "iOS Test Loop"
                    else -> null
                }
                title?.let {
                    item {
                        ExecutionItem(
                            title = stringResource(id = R.string.test_type),
                            value = title
                        )
                    }
                }
            }
            specification?.androidTest?.androidAppInfo?.let {
                item {
                    ExecutionItem(
                        title = stringResource(R.string.execution_app_name),
                        value = it.name ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    ExecutionItem(
                        title = stringResource(R.string.execution_package_name),
                        value = it.packageName ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    ExecutionItem(
                        title = stringResource(R.string.execution_version_name),
                        value = it.versionName ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    ExecutionItem(
                        title = stringResource(R.string.execution_version_code),
                        value = it.versionCode ?: stringResource(R.string.undefined)
                    )
                }
            }
            specification?.iosTest?.iosAppInfo?.let {
                item {
                    ExecutionItem(
                        title = stringResource(R.string.execution_app_name),
                        value = it.name ?: stringResource(R.string.undefined)
                    )
                }
            }
        }
    }
}

@Composable
fun EnvironmentList(
    environments: LazyPagingItems<ExecutionEnvironment>,
    setErrorState: (ExecutionErrorState) -> Unit,
    navigateToEnvironmentScreen: (String) -> Unit
) {
    when (environments.loadState.refresh) {
        is LoadState.Error -> {
            setErrorState(ExecutionErrorState.GET_ENVIRONMENT_LIST)
        }
        else -> {
            // no-op
        }
    }
    when (environments.loadState.append) {
        is LoadState.Error -> {
            setErrorState(ExecutionErrorState.GET_ENVIRONMENT_LIST)
        }
        else -> {
            // no-op
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(environments.itemCount) {
            ExecutionEnvironmentItem(
                model = environments[it]?.dimensionValue?.firstOrNull()?.value
                    ?: stringResource(R.string.undefined),
                version = environments[it]?.dimensionValue?.getOrNull(1)?.value
                    ?: stringResource(R.string.undefined),
                locale = environments[it]?.dimensionValue?.getOrNull(2)?.value
                    ?: stringResource(R.string.undefined),
                orientation = environments[it]?.dimensionValue?.getOrNull(3)?.value
                    ?: stringResource(R.string.undefined),
                outcome = environments[it]?.environmentResult?.outcome,
                navigateToEnvironmentScreen = {
                    navigateToEnvironmentScreen.invoke(
                        environments[it]?.environmentId ?: return@ExecutionEnvironmentItem
                    )
                }
            )
        }
    }
}

@Composable
fun ExecutionItem(title: String, value: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(title)
        Text(value)
    }
}

@Composable
fun ExecutionEnvironmentItem(
    model: String,
    version: String,
    locale: String,
    orientation: String,
    outcome: Outcome?,
    navigateToEnvironmentScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth().clickable { navigateToEnvironmentScreen.invoke() }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(7f)
        ) {
            Text(model)
            Text(version)
            Text(locale)
            Text(orientation)
        }
        if (outcome?.summary != null) {
            Icon(
                modifier = Modifier.weight(1f)
                    .size(24.dp),
                imageVector = outcome.summary.imageVector,
                contentDescription = null,
                tint = outcome.summary.tint
            )
        }
    }
}
