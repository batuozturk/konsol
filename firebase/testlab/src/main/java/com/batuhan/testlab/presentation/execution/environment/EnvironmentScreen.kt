package com.batuhan.testlab.presentation.execution.environment

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.testlab.R
import com.batuhan.theme.Orange

@Composable
fun EnvironmentScreen(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<EnvironmentViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.environmentEvent.collect { event ->
            when (event) {
                EnvironmentEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    EnvironmentScreenContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        retryOperation = viewModel::retryOperation,
        setSnackbarState = viewModel::setSnackbarState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentScreenContent(
    uiState: EnvironmentUiState,
    onBackPressed: () -> Unit,
    retryOperation: (EnvironmentErrorState, (() -> Unit)?) -> Unit,
    setSnackbarState: (Boolean) -> Unit
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
    val environment by remember(uiState.environment) {
        derivedStateOf { uiState.environment }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
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
                    retryOperation.invoke(errorState!!, null)
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
                        environment?.environmentId ?: ""
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
        environment?.run {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                item {
                    EnvironmentItem(
                        title = stringResource(id = R.string.environment_id),
                        value = environmentId ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    EnvironmentItem(
                        title = stringResource(id = R.string.execution_id),
                        value = executionId ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    EnvironmentItem(
                        title = stringResource(id = R.string.creation_date),
                        value = creationTime?.getDate() ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    EnvironmentItem(
                        title = stringResource(id = R.string.completion_date),
                        value = completionTime?.getDate() ?: stringResource(R.string.undefined)
                    )
                }
                environmentResult?.outcome?.summary?.let {
                    item {
                        EnvironmentItem(
                            title = stringResource(id = R.string.test_summary),
                            value = it.name
                        )
                    }
                }
                item {
                    EnvironmentItem(
                        title = stringResource(id = R.string.model),
                        value = dimensionValue?.firstOrNull()?.value
                            ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    EnvironmentItem(
                        title = stringResource(R.string.version),
                        value = dimensionValue?.getOrNull(1)?.value
                            ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    EnvironmentItem(
                        title = stringResource(id = R.string.locale),
                        value = dimensionValue?.getOrNull(2)?.value
                            ?: stringResource(R.string.undefined)
                    )
                }
                item {
                    EnvironmentItem(
                        title = stringResource(id = R.string.orientation),
                        value = dimensionValue?.getOrNull(3)?.value
                            ?: stringResource(R.string.undefined)
                    )
                }
            }
        }
    }
}

@Composable
fun EnvironmentItem(title: String, value: String) {
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
