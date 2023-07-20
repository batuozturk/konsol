package com.batuhan.management.presentation.project.settings.actions.billinginfo.add

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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.BillingAccount
import com.batuhan.theme.Orange

@Composable
fun AddBillingInfo(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<AddBillingInfoViewModel>()
    val billingAccounts = viewModel.billingAccounts.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.addBillingInfoEvent.collect { event ->
            when (event) {
                is AddBillingInfoEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    AddBillingInfoContent(
        uiState = uiState,
        billingAccounts = billingAccounts,
        onBackPressed = viewModel::onBackPressed,
        addBillingInfo = viewModel::addBillingInfo,
        selectBillingAccount = viewModel::selectBillingAccount,
        setSnackbarState = viewModel::setSnackbarState,
        setErrorState = viewModel::setErrorState,
        retryOperation = viewModel::retryOperation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillingInfoContent(
    uiState: AddBillingInfoUiState,
    billingAccounts: LazyPagingItems<BillingAccount>,
    onBackPressed: () -> Unit,
    addBillingInfo: () -> Unit,
    selectBillingAccount: (BillingAccount) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    setErrorState: (AddBillingInfoErrorState) -> Unit,
    retryOperation: (AddBillingInfoErrorState, (() -> Unit)?) -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val selectedBillingAccount by remember(uiState.selectedBillingAccount) {
        derivedStateOf { uiState.selectedBillingAccount }
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
                    retryOperation(errorState!!) {
                        billingAccounts.refresh()
                    }
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
                    Text(stringResource(id = R.string.update_billing_info))
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
                        IconButton(onClick = addBillingInfo) {
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(id = R.string.select_billing_account_title))
            when (billingAccounts.loadState.refresh) {
                is LoadState.Error -> {
                    setErrorState.invoke(AddBillingInfoErrorState.BILLING_ACCOUNTS)
                }
                else -> {
                }
            }

            when (billingAccounts.loadState.append) {
                is LoadState.Error -> {
                    setErrorState.invoke(AddBillingInfoErrorState.BILLING_ACCOUNTS)
                }
                else -> {
                }
            }
            LazyColumn {
                items(billingAccounts.itemCount) { index ->
                    BillingAccountItem(
                        billingAccount = billingAccounts[index],
                        isSelected = selectedBillingAccount?.name == billingAccounts[index]?.name,
                        selectBillingAccount = { account ->
                            selectBillingAccount.invoke(account)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BillingAccountItem(
    billingAccount: BillingAccount?,
    isSelected: Boolean,
    selectBillingAccount: (BillingAccount) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth().padding(vertical = 8.dp)
            .clickable {
                selectBillingAccount.invoke(billingAccount ?: return@clickable)
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(billingAccount?.displayName ?: stringResource(id = R.string.undefined))
        if (isSelected) Icon(
            imageVector = Icons.Default.Done,
            contentDescription = null,
            tint = Orange
        )
    }
}
