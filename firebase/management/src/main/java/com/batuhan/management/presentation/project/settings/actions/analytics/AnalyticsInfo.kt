package com.batuhan.management.presentation.project.settings.actions.analytics

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.batuhan.management.R
import com.batuhan.core.data.model.management.AnalyticsProperty
import com.batuhan.theme.Orange

@Composable
fun AnalyticsInfo(onBackPressed: () -> Unit, navigateToAddAnalyticsAccount: (String) -> Unit) {
    val viewModel = hiltViewModel<AnalyticsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.analyticsInfoEvent.collect { event ->
            when (event) {
                is AnalyticsInfoEvent.Back -> onBackPressed.invoke()
                is AnalyticsInfoEvent.NavigateToAddAnalyticsAccount -> {
                    navigateToAddAnalyticsAccount.invoke(
                        event.projectId
                    )
                }
            }
        }
    }
    AnalyticsInfoContent(
        uiState = uiState,
        onBackPressed = onBackPressed,
        addAnalyticsAccount = viewModel::addAnalyticsAccount,
        removeAnalyticsAccount = viewModel::removeAnalyticsAccount,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsInfoContent(
    uiState: AnalyticsInfoUiState,
    onBackPressed: () -> Unit,
    removeAnalyticsAccount: () -> Unit,
    addAnalyticsAccount: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (AnalyticsInfoErrorState) -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val analyticsInfo by remember(uiState.analyticsProperty) {
        derivedStateOf { uiState.analyticsProperty }
    }
    val context = LocalContext.current
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let { resId ->
            val message = context.getString(resId)
            val actionText = errorState?.actionResId?.let { context.getString(it) }
            when (
                snackbarHostState.showSnackbar(
                    message,
                    withDismissAction = actionText == null,
                    duration = SnackbarDuration.Indefinite,
                    actionLabel = actionText
                )
            ) {
                SnackbarResult.ActionPerformed -> {
                    setSnackbarState.invoke(false)
                    retryOperation.invoke(errorState!!)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                else -> {
                    // no-op
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    shape = RoundedCornerShape(10.dp),
                    snackbarData = it,
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    actionColor = Color.White
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
                    Text(stringResource(id = R.string.add_remove_google_analytics))
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
                                .padding(end = 16.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else {
                        if (analyticsInfo?.id != null) {
                            IconButton(onClick = removeAnalyticsAccount) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        } else {
                            IconButton(onClick = addAnalyticsAccount) {
                                Icon(
                                    imageVector = Icons.Default.AddChart,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        }
                    }
                }

            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 8.dp)
        ) {
            AnalyticsInfoItem(
                analyticsInfo = analyticsInfo
            )
        }
    }
}

@Composable
fun AnalyticsInfoItem(
    analyticsInfo: AnalyticsProperty?
) {
    analyticsInfo.takeIf { it?.id != null }?.let { info ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AnalyticsInfoAttributes(
                titleResId = R.string.analytics_property_name,
                value = info.displayName
            )
            AnalyticsInfoAttributes(
                titleResId = R.string.analytics_account_id,
                value = info.analyticsAccountId
            )
            AnalyticsInfoAttributes(
                titleResId = R.string.analytics_property_id,
                value = info.id
            )
        }
    }
}

@Composable
fun AnalyticsInfoAttributes(@StringRes titleResId: Int, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(titleResId))
            Text(value ?: stringResource(id = R.string.undefined))
        }
    }
}
