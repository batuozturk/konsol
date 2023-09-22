package com.batuhan.management.presentation.project.settings.actions.billinginfo

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
import com.batuhan.core.data.model.management.ProjectBillingInfo
import com.batuhan.theme.Orange

@Composable
fun BillingInfo(onBackPressed: () -> Unit, navigateToAddBillingInfo: (String) -> Unit) {
    val viewModel = hiltViewModel<BillingInfoViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.billingInfoEvent.collect { event ->
            when (event) {
                is BillingInfoEvent.Back -> onBackPressed.invoke()
                is BillingInfoEvent.SaveChanges -> viewModel.removeBillingInfo()
                is BillingInfoEvent.AddBillingInfo -> navigateToAddBillingInfo(event.projectId)
            }
        }
    }
    BillingInfoContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation,
        addBillingInfo = viewModel::addBillingInfo,
        removeBillingInfo = viewModel::removeBillingInfo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingInfoContent(
    uiState: BillingInfoUiState,
    onBackPressed: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (BillingInfoErrorState) -> Unit,
    addBillingInfo: () -> Unit,
    removeBillingInfo: () -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val billingInfo = uiState.billingInfo
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val context = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let { resId ->
            val message = context.getString(resId)
            val actionText = errorState?.actionResId?.let { context.getString(it) }
            when (
                snackbarHostState.showSnackbar(
                    message,
                    actionLabel = actionText,
                    withDismissAction = actionText == null,
                    duration = SnackbarDuration.Indefinite
                )
            ) {
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
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it, containerColor = Color.Red, contentColor = Color.White)
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
                    Text(stringResource(id = R.string.update_billing_info))
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
                        if (billingInfo?.billingEnabled == true) {
                            IconButton(onClick = removeBillingInfo) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        } else {
                            IconButton(onClick = addBillingInfo) {
                                Icon(
                                    imageVector = Icons.Default.AddCard,
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
            BillingInfoItem(
                billingInfo = billingInfo
            )
            BillingInfoDescription()
        }
    }
}

@Composable
fun BillingInfoItem(
    billingInfo: ProjectBillingInfo?
) {
    billingInfo.takeIf { it?.billingEnabled == true }?.let { info ->
        Column(modifier = Modifier.fillMaxWidth()) {
            BillingInfoAttributes(title = R.string.billing_info_name, value = info.name)
            BillingInfoAttributes(
                title = R.string.billing_account_name,
                value = info.billingAccountName
            )
        }
    }
}

@Composable
fun BillingInfoAttributes(@StringRes title: Int, value: String?) {
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
            Text(stringResource(id = title))
            Text(value ?: stringResource(id = R.string.undefined))
        }
    }
}

@Composable
fun BillingInfoDescription() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stringResource(id = R.string.billing_info_update_desc))
    }
}
