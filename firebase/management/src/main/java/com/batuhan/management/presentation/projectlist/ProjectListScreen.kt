package com.batuhan.management.presentation.projectlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.management.R
import com.batuhan.theme.KonsolFontFamily
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun ProjectListScreen(
    navigate: (event: ProjectListEvent) -> Unit
) {
    val viewModel = hiltViewModel<ProjectListViewModel>()
    val projects: LazyPagingItems<FirebaseProject> = viewModel.projects.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.projectListEventFlow.collect { event ->
            navigate(event)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                projects.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    ProjectListScreenContent(
        uiState = uiState,
        projects = projects,
        endSession = viewModel::endSession,
        createProject = viewModel::createProject,
        openProject = viewModel::openProject,
        setSnackbarState = viewModel::setSnackbarState,
        setErrorState = viewModel::setErrorState,
        setSelectedProject = viewModel::setSelectedProject,
        retryOperation = viewModel::retryOperation,
        setBottomSheetState = viewModel::setBottomSheetState,
        onRefresh = viewModel::onRefresh,
        clearErrorState = viewModel::clearErrorState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreenContent(
    uiState: ProjectListUiState,
    projects: LazyPagingItems<FirebaseProject>,
    endSession: () -> Unit,
    createProject: () -> Unit,
    openProject: (project: FirebaseProject) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    setErrorState: (ProjectListErrorState) -> Unit,
    setSelectedProject: (FirebaseProject) -> Unit,
    retryOperation: (ProjectListErrorState, (() -> Unit)?) -> Unit,
    setBottomSheetState: (Boolean) -> Unit,
    onRefresh: (() -> Unit) -> Unit,
    clearErrorState: () -> Unit
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
    val coroutineScope = rememberCoroutineScope()
    val showBottomSheet by remember(uiState.isBottomSheetOpened) {
        derivedStateOf { uiState.isBottomSheetOpened }
    }
    val modalSheetState = rememberModalBottomSheetState(
        confirmValueChange = { false },
        skipPartiallyExpanded = true
    )
    val context = LocalContext.current
    val selectedProject by remember(uiState.selectedProject) {
        derivedStateOf { uiState.selectedProject }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
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
                    retryOperation.invoke(errorState!!) {
                        projects.refresh()
                    }
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                else -> {
                    // no-op
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = Color.Red,
                    actionColor = Color.White,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name_title),
                        fontFamily = KonsolFontFamily,
                        color = Orange,
                        fontSize = 32.sp
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
                    }
                    IconButton(
                        onClick = {
                            createProject.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                    IconButton(
                        onClick = {
                            endSession.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) {
        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = {
                setSnackbarState.invoke(false)
                snackbarHostState.currentSnackbarData?.dismiss()
                onRefresh.invoke {
                    coroutineScope.launch { // this is a workaround for not refreshing problem
                        delay(1L)
                        clearErrorState.invoke()
                        projects.refresh()
                    }
                }
            },
            state = rememberSwipeRefreshState(isRefreshing)
        ) {
            when (projects.loadState.refresh) {
                is LoadState.Error -> {
                    setErrorState.invoke(ProjectListErrorState.PROJECT_LIST)
                }
                else -> {
                    // no-op
                }
            }

            when (projects.loadState.append) {
                is LoadState.Error -> {
                    setErrorState.invoke(ProjectListErrorState.PROJECT_LIST)
                }
                else -> {
                    // no-op
                }
            }
            if (projects.itemCount == 0 && errorState == null && projects.loadState.refresh is LoadState.NotLoading) {
                ProjectListEmptyView()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(projects.itemCount) {
                        projects[it]?.let { project ->
                            ProjectListItem(
                                project = project,
                                onItemClick = {
                                    openProject.invoke(project)
                                },
                                onInfoClick = {
                                    setSelectedProject.invoke(it)
                                    setBottomSheetState.invoke(true)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { setBottomSheetState.invoke(false) },
            sheetState = modalSheetState,
            dragHandle = {}
        ) {
            ProjectInfoBottomSheet(
                project = selectedProject
            ) {
                coroutineScope.launch {
                    modalSheetState.hide()
                }.invokeOnCompletion {
                    if (!modalSheetState.isVisible) {
                        setBottomSheetState.invoke(false)
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectListEmptyView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(id = R.string.project_list_empty_view_title),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KonsolTheme {
        ProjectListScreenContent(
            uiState = ProjectListUiState(),
            projects = flowOf(
                PagingData.from(
                    listOf(
                        FirebaseProject(
                            "hello",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                    )
                )
            ).collectAsLazyPagingItems(),
            endSession = {},
            createProject = {},
            openProject = {},
            setSnackbarState = {},
            setErrorState = {},
            setSelectedProject = {},
            retryOperation = { _, _ -> },
            setBottomSheetState = {},
            onRefresh = {},
            clearErrorState = {}
        )
    }
}
