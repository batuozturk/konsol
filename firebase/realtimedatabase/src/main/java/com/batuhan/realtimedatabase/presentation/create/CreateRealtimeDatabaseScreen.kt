package com.batuhan.realtimedatabase.presentation.create

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.core.data.model.management.AvailableLocation
import com.batuhan.realtimedatabase.R
import com.batuhan.theme.Orange

@Composable
fun CreateDatabaseScreen(
    onBackPressed: () -> Unit
) {
    val viewModel = hiltViewModel<CreateRealtimeDatabaseViewModel>()
    val locations = viewModel.locations.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.realtimeDatabaseEvent.collect {
            when (it) {
                is CreateDatabaseEvent.Save -> onBackPressed.invoke()
                is CreateDatabaseEvent.Back -> onBackPressed.invoke()
                else -> {}
            }
        }
    }

    CreateDatabaseScreenContent(
        uiState = uiState,
        locations = locations,
        onBackPressed = viewModel::onBackPressed,
        setErrorState = viewModel::setErrorState,
        retryOperation = viewModel::retryOperation,
        setLocation = viewModel::setLocationId,
        setName = viewModel::setName,
        createDatabase = viewModel::createDatabase,
        setSnackbarState = viewModel::setSnackbarState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDatabaseScreenContent(
    uiState: CreateDatabaseUiState,
    locations: LazyPagingItems<AvailableLocation>,
    onBackPressed: () -> Unit,
    setErrorState: (CreateDatabaseErrorState) -> Unit,
    retryOperation: (CreateDatabaseErrorState, () -> Unit) -> Unit,
    setLocation: (String) -> Unit,
    setName: (String) -> Unit,
    createDatabase: () -> Unit,
    setSnackbarState: (Boolean) -> Unit
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

    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    val name by remember(uiState.name) {
        derivedStateOf { uiState.name }
    }

    val selectedLocation by remember(uiState.selectedLocation) {
        derivedStateOf { uiState.selectedLocation }
    }

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
                    retryOperation(errorState!!) {
                        locations.refresh()
                    }
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
                title = { Text("Create Database") },
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
                    } else {
                        IconButton(onClick = { createDatabase.invoke() }) {
                            Icon(
                                imageVector = Icons.Default.Save,
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
        CreateDatabaseContentItem(
            isNameEditable = uiState.isEditable,
            modifier = Modifier.padding(it).fillMaxSize(),
            name = name,
            selectedLocation = selectedLocation,
            locations = locations,
            setLocation = setLocation,
            setName = setName
        )
    }
}

@Composable
fun CreateDatabaseContentItem(
    isNameEditable: Boolean,
    modifier: Modifier,
    name: String?,
    selectedLocation: String?,
    locations: LazyPagingItems<AvailableLocation>,
    setLocation: (String) -> Unit,
    setName: (String) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            label = { Text(stringResource(id = R.string.database_name)) },
            value = name!!,
            onValueChange = {
                setName(it)
            },
            enabled = isNameEditable,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(locations.itemCount) {
                locations[it]?.let {
                    AvailableLocationItem(
                        availableLocation = it,
                        isSelected = it.locationId == selectedLocation,
                        selectLocation = setLocation
                    )
                }
            }
        }
    }
}

@Composable
fun AvailableLocationItem(
    availableLocation: AvailableLocation,
    isSelected: Boolean,
    selectLocation: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                selectLocation(availableLocation.locationId ?: return@clickable)
            }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = availableLocation.locationId ?: stringResource(id = R.string.undefined),
                modifier = Modifier.weight(8f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
