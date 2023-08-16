package com.batuhan.firestore.presentation.databaselist

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.Database
import com.batuhan.firestore.data.model.DatabaseType
import com.batuhan.theme.Orange

@Composable
fun DatabaseListScreen(
    onBackPressed: () -> Unit,
    createDatabase: (String) -> Unit,
    navigateToDatabaseScreen: (String) -> Unit
) {
    val viewModel = hiltViewModel<DatabaseListViewModel>()
    val databaseList by viewModel.databaseList.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.databaseListEvent.collect { event ->
            when (event) {
                is DatabaseListEvent.Back -> onBackPressed.invoke()
                is DatabaseListEvent.CreateDatabase -> createDatabase.invoke(event.projectId)
                is DatabaseListEvent.NavigateToDatabaseScreen -> navigateToDatabaseScreen.invoke(
                    event.name
                )
            }
        }
    }
    DatabaseListScreenContent(
        uiState = uiState,
        databaseList = databaseList,
        onBackPressed = viewModel::onBackPressed,
        createDatabase = viewModel::createDatabase,
        navigateToDatabaseScreen = viewModel::navigateToDatabaseScreen,
        editDatabase = viewModel::setSelectedDatabase,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation,
        setErrorState = viewModel::setErrorState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseListScreenContent(
    uiState: DatabaseListUiState,
    databaseList: List<Database>,
    onBackPressed: () -> Unit,
    createDatabase: () -> Unit,
    navigateToDatabaseScreen: (String) -> Unit,
    editDatabase: (Database) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (DatabaseListErrorState) -> Unit,
    setErrorState: (DatabaseListErrorState) -> Unit
) {
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val title = context.getString(it)
            val actionText = errorState?.actionResId?.let { resId -> context.getString(resId) }
            val result = snackbarHostState.showSnackbar(
                message = title,
                actionLabel = actionText,
                withDismissAction = actionText == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.Dismissed -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.ActionPerformed -> {
                    retryOperation.invoke(errorState!!)
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
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                },
                title = {
                    Text(stringResource(id = R.string.database_list))
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
                            modifier = Modifier.padding(end = 16.dp).size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else if (databaseList.isEmpty()) {
                        IconButton(
                            onClick = {
                                createDatabase.invoke()
                            }
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
        LazyColumn(modifier = Modifier.fillMaxSize().padding(it).padding(8.dp)) {
            items(databaseList.size) { index ->
                DatabaseItem(
                    database = databaseList[index],
                    navigateToDatabaseScreen = {
                        if (databaseList[index].type == DatabaseType.DATASTORE_MODE) {
                            editDatabase.invoke(databaseList[index])
                            setErrorState.invoke(DatabaseListErrorState.DATASTORE_CONFIG)
                        } else navigateToDatabaseScreen.invoke(it)
                    }
                )
            }
        }
    }
}

@Composable
fun DatabaseItem(
    database: Database,
    navigateToDatabaseScreen: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable {
            navigateToDatabaseScreen.invoke(database.name ?: return@clickable)
        }.border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(database.name ?: "")
        Spacer(modifier = Modifier.height(10.dp))
        Text(database.uid ?: "")
    }
}
