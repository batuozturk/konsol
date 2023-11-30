package com.batuhan.realtimedatabase.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.realtimedatabase.R
import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import com.batuhan.theme.Orange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun RealtimeDatabaseScreen(
    navigateToDatabaseScreen: (databaseUrl: String) -> Unit,
    navigateToCreateDatabaseScreen: (isFirst: Boolean, projectId: String) -> Unit,
    onBackPressed: () -> Unit
) {
    val viewModel = hiltViewModel<RealtimeDatabaseViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val databases = viewModel.databases.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.realtimeDatabaseEvent.collect {
            when (it) {
                is RealtimeDatabaseEvent.Database -> navigateToDatabaseScreen.invoke(it.databaseUrl)
                is RealtimeDatabaseEvent.CreateDatabase -> navigateToCreateDatabaseScreen.invoke(
                    databases.itemCount == 0,
                    it.projectId
                )

                is RealtimeDatabaseEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                databases.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    RealtimeDatabaseScreenContent(
        uiState = uiState,
        databases = databases,
        onBackPressed = viewModel::onBackPressed,
        setErrorState = viewModel::setErrorState,
        retryOperation = viewModel::retryOperation,
        navigateToCreateDatabaseScreen = viewModel::navgiateToCreateDatabase,
        navigateToDatabaseScreen = viewModel::navigateToDatabaseScreen,
        setSnackbarState = viewModel::setSnackbarState,
        onRefresh = viewModel::setRefreshState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealtimeDatabaseScreenContent(
    uiState: RealtimeDatabaseUiState,
    databases: LazyPagingItems<DatabaseInstance>,
    onBackPressed: () -> Unit,
    setErrorState: (RealtimeDatabaseErrorState) -> Unit,
    retryOperation: (RealtimeDatabaseErrorState, () -> Unit) -> Unit,
    navigateToCreateDatabaseScreen: (isFirst: Boolean) -> Unit,
    navigateToDatabaseScreen: (databaseUrl: String) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    onRefresh: (() -> Unit) -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isSnackbarShown by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }

    val isRefreshing by remember(uiState.isRefreshing) {
        derivedStateOf { uiState.isRefreshing }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val refreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    val context = LocalContext.current
    LaunchedEffect(key1 = isSnackbarShown) {
        errorState?.titleResId?.takeIf { isSnackbarShown }?.let {
            val title = context.getString(it)
            val actionText = errorState?.actionResId?.let { context.getString(it) }
            val result = snackbarHostState.showSnackbar(
                message = title,
                actionLabel = actionText,
                withDismissAction = actionText == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    setSnackbarState.invoke(false)
                    retryOperation(errorState!!) {
                        databases.refresh()
                    }
                }

                else -> {
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.database_list_title)) },
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
                    } else if (databases.itemCount == 0 &&
                        databases.loadState.source.refresh is LoadState.NotLoading &&
                        databases.loadState.append.endOfPaginationReached
                    ) {
                        IconButton(onClick = { navigateToCreateDatabaseScreen.invoke(false) }) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                tint = Orange,
                                contentDescription = null
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            tint = Orange,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    actionColor = Color.White,
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    ) {
        when (databases.loadState.refresh) {
            is LoadState.Error -> {
                setErrorState.invoke(RealtimeDatabaseErrorState.DATABASE_LIST)
            }

            else -> {
            }
        }
        when (databases.loadState.append) {
            is LoadState.Error -> {
                setErrorState.invoke(RealtimeDatabaseErrorState.DATABASE_LIST)
            }

            else -> {
            }
        }
        SwipeRefresh(
            modifier = Modifier.padding(it),
            state = refreshState,
            onRefresh = {
                onRefresh {
                    databases.refresh()
                }
                 }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(databases.itemCount) {
                    databases[it]?.let {
                        DatabaseInstanceItem(databaseInstance = it, navigateToDatabaseScreen = navigateToDatabaseScreen)
                    }
                }
            }
        }
    }
}

@Composable
fun DatabaseInstanceItem(
    databaseInstance: DatabaseInstance,
    navigateToDatabaseScreen: (databaseUrl: String) -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp).clickable {
            navigateToDatabaseScreen.invoke(databaseInstance.databaseUrl ?: return@clickable)
        }.fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(databaseInstance.name ?: "undefined")
        Spacer(modifier = Modifier.height(10.dp))
        Text(databaseInstance.databaseUrl ?: "undefined")
        Spacer(modifier = Modifier.height(10.dp))
        Text(databaseInstance.project ?: "undefined")
    }
}
