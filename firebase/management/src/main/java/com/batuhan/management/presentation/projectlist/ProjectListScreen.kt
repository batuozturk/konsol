package com.batuhan.management.presentation.projectlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.management.R
import com.batuhan.management.presentation.projectlist.ProjectsScreenNavigationKeys.AUTH_SCREEN
import com.batuhan.management.presentation.projectlist.ProjectsScreenNavigationKeys.CREATE_PROJECT_SCREEN
import com.batuhan.management.presentation.projectlist.ProjectsScreenNavigationKeys.PROJECT_SCREEN
import com.batuhan.management.presentation.projectlist.ProjectsScreenNavigationKeys.START_DESTINATION
import com.batuhan.theme.FConsoleTheme
import com.batuhan.theme.Orange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

object ProjectsScreenNavigationKeys {
    const val START_DESTINATION = "projects_screen"
    const val PROJECT_SCREEN = "project_screen"
    const val AUTH_SCREEN = "auth_screen"
    const val CREATE_PROJECT_SCREEN = "create_project_screen"
}

@Composable
fun ProjectsScreen(
    navigate: (route: String, popUpToScreen: String?, popUpInclusive: Boolean) -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val projects: LazyPagingItems<FirebaseProject> = viewModel.projects.collectAsLazyPagingItems()
    var currentEvent: ProjectsScreenEvent? by remember { mutableStateOf(null) }
    LaunchedEffect(key1 = true) {
        viewModel.projectsScreenEventFlow.collect { event ->
            when (event) {
                is ProjectsScreenEvent.Logout -> {
                    navigate(AUTH_SCREEN, START_DESTINATION, true)
                }
                is ProjectsScreenEvent.CreateProject -> {
                    navigate(CREATE_PROJECT_SCREEN, null, false)
                }
                is ProjectsScreenEvent.Project -> {
                    navigate(
                        "$PROJECT_SCREEN/${event.project.projectId ?: ""}/${event.project.displayName ?: ""}",
                        null,
                        false
                    )
                }
                else -> {
                    currentEvent = event
                }
            }
        }
    }
    ProjectsScreenContent(
        projects = projects,
        endSession = viewModel::endSession,
        createProject = viewModel::createProject,
        openProjectInfo = viewModel::openProjectInfo,
        openProject = viewModel::openProject,
        currentEvent = currentEvent,
        closeAlert = viewModel::closeAlert,
        openAlert = viewModel::openAlert
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreenContent(
    projects: LazyPagingItems<FirebaseProject>,
    endSession: () -> Unit,
    createProject: () -> Unit,
    openProjectInfo: (project: FirebaseProject) -> Unit,
    currentEvent: ProjectsScreenEvent?,
    closeAlert: () -> Unit,
    openAlert: (String, Throwable) -> Unit,
    openProject: (project: FirebaseProject) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val modalSheetState = rememberModalBottomSheetState(
        confirmValueChange = { false },
        skipPartiallyExpanded = true
    )
    var selectedProject: FirebaseProject? by remember { mutableStateOf(null) }
    Scaffold(
        topBar = {
            TopAppBarWithCurrentEvent(
                currentEvent = currentEvent,
                closeAlert = {
                    closeAlert.invoke()
                    projects.refresh()
                },
                endSession = endSession,
                createProject = createProject,
                refreshList = {
                    projects.refresh()
                },
                coroutineScope = coroutineScope
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = Color.White
        ) {
            LazyColumn {
                when (val state = projects.loadState.refresh) { // FIRST LOAD
                    is LoadState.Error -> {
                        openAlert.invoke(state.error.message ?: "", state.error)
                    }
                    is LoadState.Loading -> { // Loading UI
                        item {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = Orange,
                                trackColor = Color.White
                            )
                        }
                    }
                    else -> {
                    }
                }

                when (val state = projects.loadState.append) { // Pagination
                    is LoadState.Error -> {
                        openAlert.invoke(state.error.message ?: "", state.error)
                    }
                    is LoadState.Loading -> {
                        item {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = Orange,
                                trackColor = Color.White
                            )
                        }
                    }
                    else -> {
                    }
                }
                items(projects.itemCount) {
                    projects[it]?.let { project ->
                        ProjectListItem(
                            project = project,
                            onItemClick = {
                                openProject.invoke(project)
                            },
                            onInfoClick = {
                                selectedProject = project
                                openProjectInfo.invoke(project)
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = modalSheetState,
            dragHandle = {}
        ) {
            when (currentEvent) {
                is ProjectsScreenEvent.ProjectDetail -> {
                    ProjectInfoBottomSheet(
                        project = selectedProject
                    ) {
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalSheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun TopAppBarActions(
    endSession: () -> Unit,
    openBottomSheet: () -> Unit,
    refreshList: () -> Unit
) {
    IconButton(
        onClick = {
            refreshList.invoke()
        }
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = Orange
        )
    }
    IconButton(
        onClick = {
            openBottomSheet.invoke()
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
}

@Composable
fun TopAppBarWithCurrentEvent(
    currentEvent: ProjectsScreenEvent?,
    closeAlert: () -> Unit,
    endSession: () -> Unit,
    createProject: () -> Unit,
    refreshList: () -> Unit,
    coroutineScope: CoroutineScope
) {
    when (currentEvent) {
        is ProjectsScreenEvent.OpenAlert -> {
            TopAppBarAlert {
                closeAlert.invoke()
            }
        }
        else -> {
            DefaultTopAppBar(
                endSession = endSession,
                createProject = createProject,
                refreshList = refreshList,
                coroutineScope = coroutineScope
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarAlert(closeAlert: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.error_occurred),
                color = Color.White
            )
        },
        actions = {
            IconButton(
                onClick = {
                    closeAlert.invoke()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Red
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    endSession: () -> Unit,
    createProject: () -> Unit,
    refreshList: () -> Unit,
    coroutineScope: CoroutineScope
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.welcome)
            )
        },
        actions = {
            TopAppBarActions(
                endSession = endSession,
                openBottomSheet = {
                    coroutineScope.launch {
                        createProject.invoke()
                    }
                },
                refreshList = refreshList
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FConsoleTheme {
        ProjectsScreenContent(
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
            openProjectInfo = {},
            currentEvent = null,
            closeAlert = {},
            openAlert = { _, _ -> }
        )
    }
}
