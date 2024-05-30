package com.batuhan.management.presentation.project.settings.actions.analytics.add

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Save
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
import com.batuhan.core.data.model.management.AnalyticsAccount
import com.batuhan.theme.Orange

@Composable
fun AddAnalyticsAccount(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<AddAnalyticsAccountViewModel>()
    val analyticsAccounts by viewModel.analyticsAccounts.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.addAnalyticsAccountEvent.collect { event ->
            when (event) {
                is AddAnalyticsAccountEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    AddAnalyticsAccountContent(
        onBackPressed = viewModel::onBackPressed,
        analyticsAccounts = analyticsAccounts,
        uiState = uiState,
        saveChanges = viewModel::saveChanges,
        selectAnalyticsAccount = viewModel::selectAnalyticsAccount,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnalyticsAccountContent(
    onBackPressed: () -> Unit,
    analyticsAccounts: List<AnalyticsAccount>,
    uiState: AddAnalyticsAccountUiState,
    saveChanges: () -> Unit,
    selectAnalyticsAccount: (AnalyticsAccount) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (AddAnalyticsAccountErrorState) -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val selectedAnalyticsAccount by remember(uiState.selectedAnalyticsAccount) {
        derivedStateOf { uiState.selectedAnalyticsAccount }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val context = LocalContext.current
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val title = context.getString(it)
            val actionTitle = errorState?.actionResId?.let { resId -> context.getString(resId) }
            val result = snackbarHostState.showSnackbar(
                message = title,
                actionLabel = actionTitle,
                withDismissAction = actionTitle == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.Dismissed -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.ActionPerformed -> {
                    retryOperation(errorState!!)
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
                    shape = RoundedCornerShape(10.dp),
                    snackbarData = it,
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    actionColor = Color.White
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_remove_google_analytics))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
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
                    } else {
                        IconButton(onClick = saveChanges) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(stringResource(id = R.string.select_analytics_account_title))
            LazyColumn {
                items(analyticsAccounts.size) { index ->
                    AnalyticsAccountItem(
                        analyticsAccount = analyticsAccounts[index],
                        isSelected = selectedAnalyticsAccount?.name == analyticsAccounts[index].name,
                        selectAnalyticsAccount = { account ->
                            selectAnalyticsAccount.invoke(account)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsAccountItem(
    analyticsAccount: AnalyticsAccount?,
    isSelected: Boolean,
    selectAnalyticsAccount: (AnalyticsAccount) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth().padding(vertical = 8.dp)
            .clickable {
                selectAnalyticsAccount.invoke(analyticsAccount ?: return@clickable)
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(analyticsAccount?.name ?: stringResource(id = R.string.undefined))
        if (isSelected) Icon(
            imageVector = Icons.Default.Done,
            contentDescription = null,
            tint = Orange
        )
    }
}
