package com.batuhan.realtimedatabase.presentation.databaseitem

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.realtimedatabase.R
import com.batuhan.theme.Orange
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseItemScreen(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<DatabaseItemViewModel>()
    val database by viewModel.database.collectAsStateWithLifecycle(initialValue = emptyMap())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler {
        viewModel.onBackPressed()
    }
    LaunchedEffect(key1 = true) {
        viewModel.realtimeDatabaseEvent.collect {
            when (it) {
                DatabaseItemEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    DatabaseItemScreenContent(
        uiState = uiState,
        database = database,
        onBackPressed = onBackPressed,
        setPath = viewModel::setPath,
        setSnackbarState = viewModel::setSnackbarState,
        setBottomSheetState = viewModel::setBottomSheetState,
        onDelete = viewModel::onDeleteClicked,
        patchData = viewModel::patchData,
        updateKey = viewModel::updateEditedKey,
        updateValue = viewModel::updateEditedValue,
        updateType = viewModel::updateEditedType,
        initPatchData = viewModel::initPatchData,
        retryOperation = viewModel::retryOperation,
        setPatchDataWithInitial = viewModel::setPatchDataWithInitial
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseItemScreenContent(
    uiState: DatabaseItemUiState,
    database: Map<String, JsonElement?>?,
    onBackPressed: () -> Unit,
    setPath: (String) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    onDelete: (String) -> Unit,
    setBottomSheetState: (Boolean) -> Unit,
    patchData: () -> Unit,
    updateKey: (String) -> Unit,
    updateValue: (String) -> Unit,
    updateType: (String) -> Unit,
    initPatchData: (String, JsonElement?) -> Unit,
    retryOperation: (DatabaseItemErrorState) -> Unit,
    setPatchDataWithInitial: () -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isSnackbarShown by remember(uiState.isSnackbarOpened) {
        derivedStateOf {
            uiState.isSnackbarOpened
        }
    }

    val isBottomSheetOpened by remember(uiState.isBottomSheetOpened) {
        derivedStateOf { uiState.isBottomSheetOpened }
    }

    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    val path by remember(uiState.path) {
        derivedStateOf { uiState.path }
    }

    val selectedType by remember(uiState.selectedType) {
        derivedStateOf { uiState.selectedType }
    }

    val selectedKey by remember(uiState.editingKey) {
        derivedStateOf { uiState.editingKey }
    }

    val selectedValue by remember(uiState.editingValue) {
        derivedStateOf { uiState.editingValue }
    }
    val isEditing by remember(uiState.isEditing) {
        derivedStateOf { uiState.isEditing }
    }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(true, confirmValueChange = { false })

    val snackbarHostState = remember {
        SnackbarHostState()
    }
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
                    retryOperation.invoke(errorState!!)
                }

                else -> {
                    setSnackbarState.invoke(false)
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.database_screen_title)) },
                actions = {
                    if (errorState.takeIf { it != DatabaseItemErrorState.KEY_EMPTY && it != DatabaseItemErrorState.VALUE_INVALID } != null) {
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
                        IconButton(onClick = {
                            setBottomSheetState.invoke(true)
                            setPatchDataWithInitial.invoke()
                        }) {
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
        DatabaseItemContent(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            path,
            database,
            setPath,
            onDelete,
            initPatchData = { key, jsonElement ->
                initPatchData(key, jsonElement)
                setBottomSheetState.invoke(true)
            }
        )
    }
    if (isBottomSheetOpened) {
        DatabaseItemBottomSheet(
            errorState = errorState,
            isEditing = isEditing,
            bottomSheetState = sheetState,
            selectedType = selectedType,
            key = selectedKey,
            value = selectedValue,
            patchData = patchData,
            onClose = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    setBottomSheetState.invoke(false)
                }
            },
            updateKey = updateKey,
            updateValue = updateValue,
            updateType = updateType
        )
    }
}

@Composable
fun DatabaseItemContent(
    modifier: Modifier,
    path: String?,
    database: Map<String, JsonElement?>?,
    setPath: (String) -> Unit,
    onDelete: (String) -> Unit,
    initPatchData: (String, JsonElement?) -> Unit
) {
    Column(modifier) {
        Text(path ?: "", modifier = Modifier.padding(8.dp))
        LazyColumn() {
            items(database?.size ?: 0) {
                DatabaseItem(
                    key = database!!.keys.elementAt(it),
                    jsonElement = database[database.keys.elementAt(it)],
                    setPath = setPath,
                    onDelete = onDelete,
                    initPatchData = initPatchData
                )
            }
        }
    }
}

@Composable
fun DatabaseItem(
    key: String,
    jsonElement: JsonElement?,
    setPath: (String) -> Unit,
    onDelete: (String) -> Unit,
    initPatchData: (String, JsonElement?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                if (jsonElement?.isJsonObject == true) {
                    setPath.invoke(key)
                }
            }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(6f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(key)
            if (jsonElement?.isJsonPrimitive == true) Text(jsonElement.toString())
        }
        IconButton(modifier = Modifier.weight(1f), onClick = {
            initPatchData.invoke(key, jsonElement)
        }) {
            Icon(
                imageVector = Icons.Default.Edit,
                tint = Orange,
                contentDescription = null
            )
        }
        IconButton(modifier = Modifier.weight(1f), onClick = { onDelete.invoke(key) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                tint = Orange,
                contentDescription = null
            )
        }
    }
}
